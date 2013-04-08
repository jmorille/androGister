scala-tools.org is in no longer serving jars. Please change your configurations to point to Sonatype's 

Maven repository at 
https://oss.sonatype.org/content/groups/scala-tools/ 
https://oss.sonatype.org/content/repositories/snapshots/ 


# Using Scala 2.9.1 and Lift 2.4 snapshot
mvn archetype:generate \
  -DarchetypeGroupId=net.liftweb \
  -DarchetypeArtifactId=lift-archetype-basic_2.9.1 \
  -DarchetypeVersion=2.4-SNAPSHOT \
  -DarchetypeRepository=https://oss.sonatype.org/content/repositories/snapshots \
  -DremoteRepositories=https://oss.sonatype.org/content/repositories/snapshots \
  -DgroupId=eu.ttbox.androgister.server \
  -DartifactId=lift-webapp \
  -Dversion=0.0.1

# Using Scala 2.9.1 and Lift 2.4-M5 (Working)
mvn archetype:generate \
  -DarchetypeGroupId=net.liftweb \
  -DarchetypeArtifactId=lift-archetype-basic_2.9.1 \
  -DarchetypeVersion=2.4-M5 \
  -DarchetypeRepository=https://oss.sonatype.org/content/groups/scala-tools   \
  -DremoteRepositories=https://oss.sonatype.org/content/groups/scala-tools  \
  -DgroupId=eu.ttbox.androgister.server \
  -DartifactId=lift-webapp \
  -Dversion=0.0.1