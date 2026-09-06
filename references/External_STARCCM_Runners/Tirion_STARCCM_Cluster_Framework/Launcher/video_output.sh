#!/bin/bash

ffmpeg --version
png_path=$1

# Using h264 codec
ffmpeg -framerate 2 -pattern_type glob -i '$png_path' -c:v libx264 -pix_fmt yuv420p out.mp4
# TODO Do this for all of the plots right after creating them.