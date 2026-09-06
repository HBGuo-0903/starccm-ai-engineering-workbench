"""
STAR-CCM+ 批量案例生成脚本

作用：
1. 读取Excel参数配置表，基于Java宏模板和SIM文件模板自动生成多个仿真案例
2. 通过config.ini配置文件配置参数映射和替换规则
3. 可添加多个需要替换参数的模板

使用流程：
1. 准备CasePlan.xlsx算例规划参数表
2. 准备template_Case.sim算例模板文件
3. 通过STARCCM+录制template_Macro.java宏模板文件
4. 准备其他需要替换参数的模板文件（可选）
3. 修改config.ini配置文件，配置参数映射和替换规则
4. 运行脚本

注意事项：
1. 需要安装第三方依赖库：pandas
2. Excel文件需包含标题行，数据从第二行开始
3. 模板文件与此脚本在同一目录下
4. 所有要进行批量替换的模板文件都要以 template_ 开头，并放在脚本所在目录
5. 需要手动修改 template_Macro.java 中待替换的字符，包括：
   - 模板文件名必须修改为 template_Macro
   - 计算结果自动导出的文件名必须修改为 CaseName
   - 算例保存路径必须修改为 SavePath
   - 待替换的值必须修改为不与宏文件中其他字符冲突的占位符
"""

import os
import shutil
import pandas as pd
import configparser
import logging
import logging.handlers
from typing import Dict, List, Optional,Tuple,Union
from datetime import datetime
from concurrent.futures import ThreadPoolExecutor, as_completed

def setup_logging():
    """初始化日志，日志路径默认为脚本所在目录下的 STARCCM+_BatchScript.log """
    log_file = os.path.join(os.path.dirname(os.path.abspath(__file__)), f"STARCCM+_BatchScript_{datetime.now().strftime('%Y%m%d_%H%M%S')}..log")
    
    # 设置RotatingFileHandler文件
    file_handler = logging.handlers.RotatingFileHandler(
        log_file,
        maxBytes=1048576,  # 1MB
        backupCount=5,
        encoding='utf-8'
    )
    # 设置日志格式
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(levelname)s - %(message)s',
        handlers=[
            file_handler,
            logging.StreamHandler()
        ]
    )

def ParamMappingCreation(MacroParamToReplace, ParamMapping):
    """
    读取Excel文件并创建参数数组

    Args：
    MacroParamToReplace : list[str]
        用户定义的待替换宏参数列表
    ParamMapping : dict
    {
        "宏参数1": "Excel列名1",
        "宏参数2": "Excel列名2",
        ...
    }
        配置字典，包含Excel列名和宏参数的映射关系

    Returns：
    MacroParamToReplaceValue : dict
    {
        "宏参数1": [值1, 值2...],
        "宏参数2": [值1, 值2...],
        ...
    }
    """
    # 设置固定默认路径
    default_name = "CasePlan.xlsx"
    script_dir = os.path.dirname(os.path.abspath(__file__))
    file_path = os.path.join(script_dir, default_name)

    # 验证文件存在性
    if not os.path.isfile(file_path):
        logging.error(f"默认文件不存在 [{file_path}]")
        return None

    # 读取Excel数据
    try:
        df = pd.read_excel(file_path)
    except Exception as e:
        logging.error(f"读取Excel失败: {str(e)}")
        return None

    # 验证列存在性
    missing_columns = [col for col in ParamMapping.values() if col not in df.columns]
    if missing_columns:
        logging.error(f"不存在的列: {', '.join(missing_columns)}")
        return None

    # 生成参数数组
    MacroParamToReplaceValue = {}
    for param in MacroParamToReplace:
        if param in ParamMapping:
            MacroParamToReplaceValue[param] = df[ParamMapping[param]].tolist()
            logging.info(f"成功映射 {param} ← {ParamMapping[param]}")
        else:
            # 设置默认值并给出警告
            MacroParamToReplaceValue[param] = [0] * len(df)
            logging.warning(f"{param} 未配置，使用默认值0")

    return MacroParamToReplaceValue

def normalize_path(path):
    """将路径中的单个反斜杠替换为正斜杠"""
    return path.replace('\\', '/')

def get_required_templates():
    """
    获取必须的核心模板文件
    
    Returns：
    dict - 模板字典：
        key (str): 显示名称（如"Macro.java"）
        value (str): 模板文件的绝对路径（POSIX格式）
    
    异常处理：
    - 当必须模板不存在时直接退出程序
    - 路径自动标准化处理
    """
    required = {
        "Macro.java": "template_Macro.java",
        "Case.sim": "template_Case.sim"
    }
    
    # 转换路径并验证存在性
    required_templates = {}
    for display_name, file_name in required.items():
        path = normalize_path(os.path.join(
            os.path.dirname(__file__), 
            file_name
        ))
        if not os.path.isfile(path):
            logging.error(f"关键错误：必须模板 {display_name} 不存在 ({path})")
            exit(1)
        required_templates[display_name] = path
    return required_templates

def get_custom_templates(
    replace_rules: Union[Dict[str, str], None] = None,
) -> Optional[Tuple[Dict[str, str], Dict[str, str]]]:
    """
    获取用户自定义模板并配置替换规则（非交互式版本）
    
    Args:
        replace_rules: 自定义替换规则字典，为None时使用默认规则
    
    Returns:
        tuple | None: 
            - 成功时返回元组 (custom_templates, replace_rules)
            - 无可用模板时返回 None
            - custom_templates: 字典 {显示名称: 模板文件绝对路径}
            - replace_rules: 字典 {原文本: 新文本}
    """
    # 确定模板目录
    search_dir = os.path.dirname(os.path.abspath(__file__))
    
    # 必须模板白名单
    required_template_names = ["template_Macro.java", "template_Case.sim"]    

    # 扫描所有以 template_ 开头的文件，并排除必须模板
    custom_templates = {}
    for filename in os.listdir(search_dir):
        # 跳过必须模板
        if filename in required_template_names:
            continue
            
        if filename.startswith("template_") and os.path.isfile(os.path.join(search_dir, filename)):
            # 提取显示名称（去掉前缀）
            logging.info('发现自定义模板：')
            logging.info(filename)
            display_name = filename[9:]  # 去掉 template_ 前缀
            custom_templates[display_name] = os.path.abspath(os.path.join(search_dir, filename))
    
    if not custom_templates:
        return {}, {}

    # 设置替换规则（使用传入规则或默认规则）
    final_replace_rules = {
        "CaseName": "CASE_NUMBER",  # 默认规则
        # 可添加其他默认规则...
    }
    
    if replace_rules is not None:
        final_replace_rules.update(replace_rules)
    
    return custom_templates, final_replace_rules

def CreatOutputFolder(OutputFolderPath: str): # 需要修改if条件
    """
    获取用户指定的输出路径，自动创建不存在的目录

    Args:
        OutputFolderPath: 用户指定的输出路径，可以为空字符串

    Returns:
        str: 输出目录的绝对路径
    
    功能：
    - 处理路径的合法性验证
    - 自动创建目标目录
    
    交互流程：
    1. 显示默认输出路径（脚本所在目录下的batch_cases文件夹）
    2. 接收配置文件路径或使用默认路径
    3. 将路径转换为绝对路径格式
    4. 尝试创建目录（包含多层目录创建）
    """
    default_folder = os.path.join(os.path.dirname(os.path.abspath(__file__)), "batch_cases")
    logging.info(f"=== 输出路径配置 ===")
    logging.info(f"默认输出路径：{default_folder}")
    if OutputFolderPath:
        user_config_path = os.path.abspath(OutputFolderPath)
        logging.info(f"用户配置文件路径：{user_config_path}")
        output_path = user_config_path

    try:
        # 自动创建不存在的目录
        os.makedirs(output_path, exist_ok=True)
        logging.info(f"输出目录已确认：{output_path}")
        return output_path
    except Exception as e:
        logging.error(f"输出目录创建失败：{str(e)}")
        logging.error("请检查路径合法性或权限设置")

def process_required_templates(required_templates, MacroParamToReplaceValue, output_folder_path):
    """
    处理必须的核心模板文件（template_Macro.java和template_Case.sim）

    Args：
        required_templates : dict
            必须模板字典，结构为：
            {
                "Macro.java": "template_Macro.java的绝对路径",
                "Case.sim": "template_Case.sim的绝对路径"
            }
        MacroParamToReplaceValue : dict
            从Excel读取的参数数据集，结构为：
            {
                "参数名1": [值1, 值2...],
                "参数名2": [值1, 值2...]
            }
        output_folder_path : str
            输出目录的绝对路径

    Returns：
        tuple: 包含两个列表的元组 (sim_files, java_files)
            sim_files: 生成的.sim文件路径列表
            java_files: 生成的.java宏文件路径列表

    处理逻辑：
        1. 根据数据行数确定案例编号格式（补零位数）
        2. 遍历每个案例的数据行
        3. 对每个必须模板文件进行：
           - 路径标准化处理
           - 文件名生成（包含案例编号）
           - 内容替换（参数值和固定规则）
           - 特殊处理.java文件的类名和保存路径
    """
    sim_files = []
    java_files = []

    # 循环处理每一行的数据
    num_rows = min(len(v) for v in MacroParamToReplaceValue.values())
    for row_index in range(num_rows):
        # 生成序号，根据 num_rows 的位数确定序号的位数
        num_digits = len(str(num_rows))
        case_number = f"Case{row_index + 1:0{num_digits}d}"

        # 动态处理所有模板
        for display_name, template_path in required_templates.items():
            if display_name.endswith(".sim"): 
                # 单独设置 sim 文件命名规则
                output_filename = f"{case_number}.sim"
                output_path = normalize_path(f"{output_folder_path}/{output_filename}")
                # 复制模板文件
                shutil.copy(template_path, output_path)
                # 添加到sim文件列表
                sim_files.append(output_path) 
            elif display_name.endswith(".java"): 
                output_filename = f"{os.path.splitext(display_name)[0]}_{case_number}{os.path.splitext(display_name)[1]}"
                output_path = normalize_path(f"{output_folder_path}/{output_filename}")
                # 复制模板文件
                shutil.copy(template_path, output_path)
                # 添加到java文件列表
                java_files.append(output_path) 
                # java模板的特殊替换逻辑
                with open(output_path, 'r+', encoding='utf-8', newline='\n') as file:
                    content = file.read()
                    content = content.replace("CaseName", case_number)
                    content = content.replace("template_Macro", os.path.splitext(output_filename)[0])
                    content = content.replace("SavePath", normalize_path(f"{output_folder_path}/{case_number}.sim"))
                    for column_name, column_value in MacroParamToReplaceValue.items():
                        content = content.replace(column_name, str(column_value[row_index]))
                    file.seek(0)
                    file.write(content)
                    file.truncate()
    
    return sim_files, java_files

def process_sim_command(sim_files, java_files, output_folder_path, SimParallelNumber, max_threads):
    """
    批量执行 Star-CCM+ 命令(支持多线程)
    
    Args:
        sim_files : list[str]
            SIM仿真文件路径列表，每个元素为绝对路径字符串
            示例：["D:/cases/Case1.sim", "D:/cases/Case2.sim"]

        java_files : list[str]
            Java宏文件路径列表，与sim_files一一对应
            示例：["D:/cases/Macro_Case1.java", ...]
        
        output_folder_path : str
            输出目录路径，用于存放执行日志
            示例："D:/cases/execution_logs"
        
        SimParallelNumber : int
            starccm并行执行进程数
        
        max_threads : int
            同时执行的cmd命令数量
    Returns:
        None
        注意：函数包含系统命令执行，实际使用需根据STAR-CCM+环境配置调整执行命令

    主要功能：
        1. 验证SIM/Java文件对应关系
        2. 创建带时间戳的日志目录
        3. 使用线程池并行执行命令
    """
    # 验证文件对应关系
    if len(sim_files) != len(java_files):
        logging.error(f"SIM文件({len(sim_files)})和JAVA文件({len(java_files)})数量不匹配")
        return
    else:
        logging.info(f"SIM文件({len(sim_files)})和JAVA文件({len(java_files)})数量匹配")

    # 创建执行日志目录
    log_dir = os.path.join(output_folder_path, "execution_logs")
    os.makedirs(log_dir, exist_ok=True)
    
    # 定义执行单个案例的函数
    def execute_case(sim_file, java_file):
        try:
            case_number = os.path.splitext(os.path.basename(sim_file))[0]
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            log_file = os.path.join(log_dir, f"{case_number}_{timestamp}.log")

            # 构建执行命令
            starccm_command = (
                f'cd /d "{output_folder_path}" && '
                f'starccm+ -np {SimParallelNumber} -power "{os.path.basename(sim_file)}" '
                f'-batch "{os.path.basename(java_file)}" '
                f'>> "{log_file}" 2>&1'
            )

            logging.info(f"开始执行案例 {case_number}...")
            logging.info(f"日志文件: {log_file}")
            
            # 使用subprocess执行命令
            exit_code = os.system(starccm_command)
            
            if exit_code == 0:
                logging.info(f"成功执行：{case_number}")
                # 清除备份文件
                backup_file = os.path.join(output_folder_path, f"{case_number}.sim~")
                if os.path.exists(backup_file):
                    os.remove(backup_file)
                return True
            else:
                logging.error(f"执行失败：{case_number}，退出码：{exit_code}")
                return False
                
        except Exception as e:
            logging.error(f"处理 {sim_file} 时发生错误：{str(e)}")
            return False

    # 使用线程池执行所有案例
    with ThreadPoolExecutor(max_workers=max_threads) as executor:
        futures = {
            executor.submit(execute_case, sim, java): (sim, java)
            for sim, java in zip(sim_files, java_files)
        }
        
        # 等待所有任务完成并处理结果
        success_count = 0
        for future in as_completed(futures):
            sim_file, java_file = futures[future]
            case_number = os.path.splitext(os.path.basename(sim_file))[0]
            try:
                if future.result():
                    success_count += 1
            except Exception as e:
                logging.error(f"案例 {case_number} 执行异常: {str(e)}")

    logging.info(f"执行完成: 成功 {success_count}/{len(sim_files)} 个案例")

def process_custom_templates(custom_templates, MacroParamToReplaceValue, output_folder_path, replace_rules):
    """
    处理用户自定义模板文件

    Args：
        custom_templates : dict
            自定义模板字典，结构为：
            {
                "显示名称1": "模板文件1绝对路径",
                "显示名称2": "模板文件2绝对路径"
            }
        MacroParamToReplaceValue : dict
            从Excel读取的参数数据集，结构同process_required_templates
        output_folder_path : str
            输出目录的绝对路径
        replace_rules : dict
            用户定义的文本替换规则，格式：
            {"旧文本": "新文本", ...}
            新文本可能包含占位符（如 CASE_NUMBER 会在后续替换为实际值）

    Returns：
        list: 生成的文件路径列表

    处理逻辑：
        1. 根据数据行数确定案例编号格式（补零位数）
        2. 遍历每个案例的数据行
        3. 对每个自定义模板文件进行：
           - 路径标准化处理
           - 文件名生成（包含案例编号）
           - 内容替换（参数值和用户自定义规则）
           - 自动替换CASE_NUMBER为实际案例编号

    注意事项：
        1. 模板文件名必须使用 template_ 前缀
        2. 显示名称会自动去掉 template_ 前缀
        3. 替换规则默认包含 CaseName:CASE_NUMBER
    """
    generated_files = []
    
    num_rows = min(len(v) for v in MacroParamToReplaceValue.values())
    for row_index in range(num_rows):
        num_digits = len(str(num_rows))
        case_number = f"Case{row_index + 1:0{num_digits}d}"
        
        for display_name, template_path in custom_templates.items():
            output_filename = f"{os.path.splitext(display_name)[0]}_{case_number}{os.path.splitext(display_name)[1]}"
            output_path = normalize_path(f"{output_folder_path}/{output_filename}")
            shutil.copy(template_path, output_path)
            generated_files.append(output_path)
            
            with open(output_path, 'r+', encoding='utf-8', newline='\n') as file:
                content = file.read()
                content = content.replace("CaseName", case_number)
                for old, new in replace_rules.items():
                    if new == "CASE_NUMBER":
                        new = case_number
                    content = content.replace(old, new)
                content = content.replace('template_'+os.path.splitext(str(display_name))[0], os.path.splitext(output_filename)[0])
                file.seek(0)
                file.write(content)
                file.truncate()
    
    return generated_files

def read_config_file(return_structured=False):
    '''
    读取配置文件
    
    Args:
        return_structured: bool, optional
            是否返回结构化数据，默认为False
    
    Returns:
        dict | None: 结构化数据字典，包含以下键值：
            - MacroParamToReplace: list[str]
                待替换参数名列表
            - ParamMapping: dict[str, str]
                参数名映射表，格式为 {参数名: 映射列名}
            - OutputFolder: str
                输出目录路径
            - ReplaceRules: dict[str, str]
                自定义替换规则，格式为 {旧文本: 新文本}
            - BatchState: dict[str, bool]
                批处理状态，格式为 {状态名: 状态值}
                - process_required_templates: bool
                    是否处理必需模板文件
                - process_sim_command: bool
                    是否执行 STAR-CCM+ 批处理命令
                - process_custom_templates: bool
                    是否处理用户自定义模板文件
    '''
    config = configparser.ConfigParser()
    # Preserve original case for all keys by overriding optionxform
    config.optionxform = lambda option: option
    config_file = 'config.ini'
    
    if not os.path.exists(config_file):
        logging.error(f"错误: {config_file} 不在当前目录中")
        return None if return_structured else None
    
    try:
        with open(config_file, 'r', encoding='utf-8') as f:
            config.read_file(f)
        
        if not return_structured:
            logging.info("成功读取 config.ini (UTF-8编码):")
            logging.info("-" * 40)
            for section in config.sections():
                logging.info(f"[{section}]")
                for key, value in config.items(section):
                    logging.info(f"{key} = {value}")
                logging.info("-" * 40)
        
        # Convert to structured data
        structured_data = {
            'MacroParamToReplace': [
                param.strip() 
                for param in config.get('Settings', 'MacroParamToReplace').split(',')
            ],
            'OutputFolder': config.get('Settings', 'OutputPath').strip('"'),
            'MaxThreads': int(config.get('Settings','MaxThreads', fallback=4)),
            'SimParallelNumber': int(config.get('Settings','SimParallelNumber', fallback=1)),
            'ParamMapping': dict(config.items('ParamMapping')),
            'ReplaceRules': dict(config.items('ReplaceRules')) if 'ReplaceRules' in config and config.items('ReplaceRules') else None,
            'BatchState': {
                k: v.lower() == 'true'
                for k, v in config.items('BatchState')
            } if 'BatchState' in config else None            
        }
        
        if structured_data is None:
            return
        if structured_data:
            logging.info(f"MacroParamToReplace = {structured_data['MacroParamToReplace']}")
            logging.info(f"OutputFolder = {os.path.abspath(structured_data['OutputFolder'])}")
            logging.info(f"MaxThreads = {structured_data['MaxThreads']}")
            logging.info(f"SimParallelNumber = {structured_data['SimParallelNumber']}")
            logging.info(f"ParamMapping = {structured_data['ParamMapping']}")
            logging.info(f"ReplaceRules = {structured_data['ReplaceRules']}")
            logging.info(f"BatchState = {structured_data['BatchState']}")    

        return structured_data if return_structured else None
    except Exception as e:
        print(f"Error reading {config_file}: {str(e)}")
        return None if return_structured else None

def MainProgram():
    # 初始化日志系统
    setup_logging()
    logging.info('=== STAR-CCM+ 批处理程序启动 ===')    
    # 第一步，读取配置文件
    structured_data = read_config_file(return_structured=True)
    
    # 第二步，读取Excel数据并建立映射
    MacroParamToReplaceValue = ParamMappingCreation(structured_data['MacroParamToReplace'], structured_data['ParamMapping'])

    # 第三步，获得必需模板文件（sim文件和macro.java文件）
    required_templates = get_required_templates()

    # 第四步，获得用户自定义模板文件
    custom_templates, ReplaceRules = get_custom_templates(structured_data['ReplaceRules'])

    # 第五步，创建输出目录
    OutputFolderPath=CreatOutputFolder(structured_data['OutputFolder'])
    
    # 第六步，处理必需模板文件
    if structured_data['BatchState']['process_required_templates']:
        logging.info('=== 处理必需模板文件 ===')
        sim_files, java_files = process_required_templates(required_templates, MacroParamToReplaceValue, OutputFolderPath)
        logging.info("生成的.sim文件：")
        for sim_file in sim_files:
            logging.info(sim_file)
        logging.info("生成的.java宏文件：")
        for java_file in java_files:
            logging.info(java_file)
        # 第七步，执行 STAR-CCM+ 批处理命令
        if structured_data['BatchState']['process_sim_command']:
            logging.info('=== 执行 STAR-CCM+ 批处理命令 ===')            
            process_sim_command(sim_files, java_files, OutputFolderPath, structured_data['SimParallelNumber'], structured_data['MaxThreads'])
            logging.info('=== STAR-CCM+ 批处理命令执行完毕 ===')
        else:
            logging.info('=== 跳过执行 STAR-CCM+ 批处理命令 ===')
    else:
        logging.info('=== 跳过处理必需模板文件 ===')

    # 第八步，处理用户自定义模板文件    
    if structured_data['BatchState']['process_custom_templates']:
        logging.info('=== 处理用户自定义模板文件 ===')
        custom_files = process_custom_templates(custom_templates, MacroParamToReplaceValue, OutputFolderPath, ReplaceRules)
        logging.info("生成的自定义文件：")
        for custom_file in custom_files:
            logging.info(custom_file)
    else:
        logging.info('=== 跳过处理用户自定义模板文件 ===')
    
    logging.info('=== 处理完毕 ===')

# 主程序入口
if __name__ == "__main__":
    MainProgram()