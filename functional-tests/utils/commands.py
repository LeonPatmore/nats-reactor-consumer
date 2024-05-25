import logging
import subprocess


def run(command: str) -> subprocess.Popen:
    logging.info(f"Running command {command}")
    with open("stdout.txt", "wb") as out, open("stderr.txt", "wb") as err:
        process = subprocess.Popen(command, stdout=out, stderr=err, shell=True)
    return process
