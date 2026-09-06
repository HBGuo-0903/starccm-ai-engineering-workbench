import os
import logging

def setup_logger():
    os.makedirs("logs", exist_ok=True)
    logging.basicConfig(level=logging.INFO,filename='logs/batch_run.log',filemode='a',format='%(asctime)s - %(levelname)s - %(message)s')
    return logging.getLogger(__name__)
