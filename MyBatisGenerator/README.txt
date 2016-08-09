installare in maven il driver ojdbc6

cd C:\a\projects\intesa\successioni\src\MyBatisGenerator

mvn install:install-file -Dfile=C:\a\software\Oracle12.1.3\oracle_common\modules\oracle.jdbc_12.1.0\ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=12.1.3 -Dpackaging=jar

OPPURE se dà errore e non trova il file provare con (Oracle_12.1.3) al posto di (Oracle12.1.3):

mvn install:install-file -Dfile=C:\a\software\Oracle_12.1.3\oracle_common\modules\oracle.jdbc_12.1.0\ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=12.1.3 -Dpackaging=jar
