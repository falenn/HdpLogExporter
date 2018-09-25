#!/bin/bash


gnuplot << ANSWERS
set style line 1 \
    linecolor rgb '#0060ad'
plot "simpleHeaderTest.dat" using 1:3 title 'SimpleDuration', "RQLHeaderTest.dat" using 1:3 title 'RQLDuration'
ANSWERS

read -n 1 -s -r -p "Press any key to continue"
