#!/bin/bash

FILENAME=output.dat
cat << "EOF" > plotdat.p
set style line 1 \
    linecolor rgb '#0060ad' 
plot "output.dat" using 1:1 title 'StartTime', "output.dat" using 1:3 title 'Duration'
EOF

gnuplot << ANSWERS
set style line 1 \
linecolor rgb '#0060ad'
plot "output.dat" using 1:1 title 'StartTime', "output.dat" using 1:3 title 'Duration'
ANSWERS

read -n 1 -s -r -p "Press any key to continue"
