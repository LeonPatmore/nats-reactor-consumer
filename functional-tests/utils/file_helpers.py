import os


def delete_file_if_exists(file: str):
    try:
        os.remove(file)
    except OSError:
        pass
