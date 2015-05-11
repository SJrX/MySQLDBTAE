#!/bin/bash

#echo "#!/bin/bash" > /ubc/cs/research/arrow/seramage/submit.sh
#echo "source /ubc/cs/home/s/seramage/.bash_profile" >> /ubc/cs/research/arrow/seramage/submit.sh 
#echo "qsub -S /bin/bash -t 1-20 -o /ubc/cs/research/arrow/seramage/ -e /ubc/cs/research/arrow/seramage/ /ubc/cs/home/s/seramage/arrowspace/software//mysqldbtae-v0.92.00b-development-100/mysql-#worker --pool $6 --mysql-database $5 --mysql-username $1 --mysql-password $2 --mysql-hostname $3 --mysql-port $4" >> /ubc/cs/research/arrow/seramage/submit.sh
#echo -e "qsub -q all.q -P eh -S /bin/bash -t 1-20 -o /ubc/cs/research/arrow/seramage/ -e /ubc/cs/research/arrow/seramage/ /ubc/cs/home/s/seramage/arrowspace/software//mysqldbtae-v0.92.00b-development-100/mysql-worker --pool $6 --mysql-database $5 --mysql-username $1 --mysql-password $2 --mysql-hostname $3 --mysql-port $4" | ssh paxos ssh arrowhead bash -l -s 
