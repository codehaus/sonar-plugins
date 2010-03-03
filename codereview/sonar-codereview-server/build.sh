clear

cd /home/cmoraes/apps/sonar-1.10.1/bin/linux-x86-32
./sonar.sh stop &

cd /home/cmoraes/code/dev/sonar/sonar-plugin-codereview
if [ "x$1" = "xclean" ]; then
   mvn clean install
else 
   mvn install
fi

if [ $? -eq 0 ]; then
   ./deploy.sh
   cd /home/cmoraes/apps/sonar-1.10.1/logs
   cat sonar.log >> 0sonar.log
   > sonar.log

   cd /home/cmoraes/apps/sonar-1.10.1/bin/linux-x86-32
   echo "========================="
   ./logwatcher.sh &

   echo "========================="
   ./sonar.sh start &
else
   notify-send --icon=/home/cmoraes/Pictures/sonar-shot.png --urgency=critical "Build Failed" "Build Failed. Check" &
fi
