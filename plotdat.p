set style line 1 \
    linecolor rgb '#0060ad' 
plot "RQLHeaderTest.dat" using 1:3 title 'RQLDuration', "simpleHeaderTest.dat" using 1:3 title 'SimpleDuration'
