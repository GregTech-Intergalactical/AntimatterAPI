from os import listdir,rename
import argparse

parser = argparse.ArgumentParser(description='Model renamer.')
parser.add_argument('file')
res = parser.parse_args()
file = res.file
paths = ["battery_buffer_four", "battery_buffer_one", "battery_buffer_nine"]

for folder in listdir(file):
    path = file + "/" + folder + "/"
    try:
        rename(path + "front.png", path + "front.pngg")
        rename(path + "back.png", path + "front.png")
        rename(path + "front.pngg", path + "back.png")

        rename(path + "active/" + "front.png", path + "active/" + "front.pngg")
        rename(path + "active/" + "back.png", path + "active/" + "front.png")
        rename(path + "active/" + "front.pngg", path + "active/" +"back.png")
    except Exception:
        print("exception " + path)