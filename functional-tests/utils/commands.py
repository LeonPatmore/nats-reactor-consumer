import logging
import subprocess

from utils.file_helpers import delete_file_if_exists


def run(command: str, log_file_postfix: str) -> subprocess.Popen:
    logging.info(f"Running command {command}")
    std_out_file = f"stdout{log_file_postfix}.txt"
    std_err_file = f"stderr{log_file_postfix}.txt"
    delete_file_if_exists(std_out_file)
    delete_file_if_exists(std_err_file)
    with open(std_out_file, "wb") as out, open(std_err_file, "wb") as err:
        process = subprocess.Popen(command, stdout=out, stderr=err, shell=True)
    return process
