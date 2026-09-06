import json
from star_runner import run_simulation
from logger_config import setup_logger

def main():
    logger = setup_logger()
    logger.info("Starting batch Execution")

    with open("batch_config.json", "r") as file:
       data = json.load(file)

    star_executable = data["star_executable"]
    template_sim = data["template_sim"]
    java_macro = data["java_macro"]
    simulations = data["simulations"]

    for sim in simulations:
        run_simulation(sim,logger,star_executable,template_sim,java_macro)

    logger.info("Batch Execution completed")

if __name__ == "__main__":
    main()

