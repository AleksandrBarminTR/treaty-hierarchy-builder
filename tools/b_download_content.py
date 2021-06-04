import xml.etree.ElementTree as ET
import sys
import requests
import urllib.parse
from os import walk
from os import path


def check_arguments():
    if len(sys.argv) != 2:
        print("Usage:")
        print("python b_download_content.py <folder_with_celex_numbers>")
        exit(1)


def download_notice(target_directory: str, celex: str):
    """
    Downloading a single Notice from EurLex
    :param target_directory:
    :param celex:
    :return:
    """
    celex_part = urllib.parse.quote_plus(celex)
    url = f"http://publications.europa.eu/resource/celex/{celex_part}"
    headers = {
        "Accept": "application/xml;notice=branch",
        "Accept-Language": "en"
    }
    print(f"Downloading notice for celex {celex}")
    file_name = celex.replace("/", "_")
    file_name = file_name.replace("(", "_")
    file_name = file_name.replace(")", "_")
    file_path = path.join(target_directory, f"{file_name}_notice.xml")
    if path.exists(file_path):
        print(f"File exists, skipping")
        return
    response = requests.get(url, headers=headers)
    if response.status_code == 200:
        with open(file_path, "w", encoding="utf-8") as target:
            target.write(response.text)


def download_notices(target_directory: str, parent_celex: str):
    """
    Downloading notices for all the files in the list
    :param target_directory:
    :return:
    """
    celex_list_file = path.join(target_directory, f"{parent_celex}.csv")
    print(f"Reading content from file {parent_celex}.csv")
    with open(celex_list_file, "r", encoding="utf-8") as src:
        for celex in src.readlines():
            if celex is None:
                continue
            celex = celex.replace("\n", "")
            download_notice(target_directory, celex)


def download_html(target_directory: str, celex: str):
    """
    Download a single HTML file
    :param notice_path:
    :param target_directory:
    :param celex:
    :return:
    """
    celex_part = urllib.parse.quote_plus(celex)
    print(f"Downloading HTML for celex {celex}")
    file_name = celex.replace("/", "_")
    file_name = file_name.replace("(", "_")
    file_name = file_name.replace(")", "_")
    url = f"http://publications.europa.eu/resource/celex/{celex_part}"
    content_types = {
        "text/html": ".html",
        "application/xhtml+xml": ".xhtml",
        "application/xml;type=fmx4": ".xml"
    }
    for content_type in content_types.keys():
        extension = content_types[content_type]
        # Generating file name
        file_path = path.join(target_directory, f"{file_name}_full_text{extension}")
        if path.exists(file_path):
            print(f"File exists, skipping")
            return
        # Building headers
        headers = {
            "Accept": content_type,
            "Accept-Language": "en"
        }
        response = requests.get(url, headers=headers)
        # If the content exists, it's ok
        if response.status_code == 200:
            with open(file_path, "w", encoding="utf-8") as target:
                target.write(response.text)
            return
        elif response.status_code == 404:
            # Nothing to do
            print(f"No content for {celex}")
        elif response.status_code == 406:
            # No content of the request type
            print(f"No content of {content_type} type for celex {celex}")
        else:
            print(f"Other status {response.status_code}")


def download_htmls(target_directory: str, parent_celex: str):
    """
    The function should download HTMLs for docs stored in the given folder
    :param target_directory:
    :return:
    """
    celex_list_file_path = path.join(target_directory, f"{parent_celex}.csv")
    print(f"Reading content from file {parent_celex}.csv")
    with open(celex_list_file_path, "r", encoding="utf-8") as src:
        for line in src.readlines():
            if line is None:
                continue
            line = line.replace("\n", "")
            download_html(target_directory, line)


def download_content(folder_path: str):
    """
    This function should download notices for every celex in the list
    and next download content if available.
    :param folder_path:
    :return:
    """
    for (dir_path, dir_names, file_names) in walk(folder_path):
        for dir_name in dir_names:
            target_directory = path.join(folder_path, dir_name)
            # Downloading content
            download_notices(target_directory, dir_name)
            download_htmls(target_directory, dir_name)


if __name__ == '__main__':
    check_arguments()
    download_content(sys.argv[1])
