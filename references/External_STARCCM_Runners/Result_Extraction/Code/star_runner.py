import os
import shutil
import subprocess
import csv


def run_simulation(config,logger,star_executable,template_sim,java_macro):
    
    case_name = config["case_name"]
    mesh_size = config["mesh_size"]
    
    logger.info(f"Running STAR case: {case_name}")
    try:
        new_case_file = f"{case_name}.sim"
        if not os.path.exists(template_sim):
          raise FileNotFoundError(f"Template {template_sim} not found")
        
        shutil.copy(template_sim, new_case_file)

        with open(java_macro, "r") as f:
          macro_content = f.read()

        macro_content = macro_content.replace("{{MESH_SIZE}}",str(mesh_size))
        macro_content = macro_content.replace("{{CASE_NAME}}",str(case_name))
        macro_content = macro_content.replace("{{MACRO_NAME}}",str(case_name)+"_macro")
        macro_file = f"{case_name}_macro.java"

        with open(macro_file, "w") as f:
          f.write(macro_content)
    
        command = [
            star_executable,
            "-batch",
            macro_file,
            new_case_file
        ]
     
        result = subprocess.run(command,
        capture_output=True,
        text=True,
        check=True)

        if result.stdout:
         logger.info(result.stdout)

        if result.stderr:
         logger.error(result.stderr)
     
        pd_csv_file = f"pd_plot_{case_name}.csv"

        with open(pd_csv_file, 'r', newline='', encoding='utf-8') as csvfile:
         reader = csv.reader(csvfile, delimiter=',')
         last_row = None
         for row in reader:
           last_row = row

         second_column_value = last_row[1] if last_row else None

         os.makedirs("Reports",exist_ok = True)
          
         summary_file_name = "Reports/batch_summary.txt"

         with open(summary_file_name, "a") as s:
           s.write(f"Pressure drop between inlet and outlet for {case_name} is {second_column_value} \n")

    except Exception as e:
        logger.error(f"Error in execution {e}")
        raise
