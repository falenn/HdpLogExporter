#set term dumb
set style line 1 \
    linecolor rgb '#0060ad'
set datafile separator "\t"
set grid
set logscale y
plot "simpleHeaderTest.dat" using 1:2 title 'Duration'
set term png
set output "DurationTest.png"
plot "RQLHeaderTest.dat" using 1:2 title 'RQLDuration'
set term png
set output "RQLDurationTest.png"
plot "RQLHeaderTest.dat" using 1:2 title 'RQL', "simpleHeaderTest.dat" using 1:2 title 'Simple'
set term x11
