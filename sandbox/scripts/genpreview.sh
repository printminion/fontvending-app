#!/bin/bash

for i in *.ttf; 
do 
FONTNAME0="`basename $i .ttf`";
#echo $FONTNAME 


#replace not needed stuff
FONTNAME=${FONTNAME0/-Regular/}
FONTNAME=${FONTNAME/_Regular/}
FONTNAME=${FONTNAME/-Bold/}
FONTNAME=${FONTNAME/-Light/}
FONTNAME=${FONTNAME/-Latin/}
FONTNAME=${FONTNAME/-Medium/}
FONTNAME=${FONTNAME/-Roman/}
FONTNAME=${FONTNAME/-Heavy/}
FONTNAME=${FONTNAME/-Book/}
FONTNAME=${FONTNAME/_/ }

#convert -font "$i" -pointsize 64 label:"$FONTNAME" -resize x64  ../preview/"$FONTNAME".png

#convert -size 480x64 -transparent white -font "$i" -pointsize 48 label:"$FONTNAME" ../preview/"$FONTNAME".png

convert -transparent white -font "$i" -pointsize 120 label:"$FONTNAME" -resize '470x64+10' ../preview/"$FONTNAME".png

done

