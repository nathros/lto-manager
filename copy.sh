#echo error test >&2
#
#END=1000
#for ((i=1;i<=END;i++)); do
#   echo $i
#	sleep 0.25
#done
rm testfilecopy
rsync -ah --progress testfile testfilecopy