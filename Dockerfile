FROM dockerfile/ubuntu

#install java
RUN apt-get update
RUN apt-get install -y openjdk-7-jdk

#install tomcat
WORKDIR /opt
RUN wget http://apache.arvixe.com/tomcat/tomcat-8/v8.0.9/bin/apache-tomcat-8.0.9.tar.gz
RUN tar -zxvf apache-tomcat-8.0.9.tar.gz
RUN mv apache-tomcat-8.0.9 tomcat

#install Maven
RUN apt-get install -y maven

#Download and install the latest branch(jackson2) of basiclti-util-java
#TODO: this will need to change when jackson2 becomes available
WORKDIR /root
RUN git clone https://github.com/IMSGlobal/basiclti-util-java.git
WORKDIR /root/basiclti-util-java
RUN git checkout jackson2
RUN mvn clean install

#build mock-rest
WORKDIR /root
ADD ./ /root/lti-example
WORKDIR /root/lti-example
RUN mvn clean install

#configure mock-rest to be the root of its virtual host
WORKDIR /opt/tomcat
ADD ./config/server.xml /opt/tomcat/conf/server.xml
RUN rm -rf webapps/*

#deploy mock-rest
RUN cp /root/lti-example/target/ExampleLtiApp*.war /opt/tomcat/webapps/ROOT.war

#run tomcat
WORKDIR /opt/tomcat/bin
ADD ./run.sh /usr/local/bin/run
EXPOSE 8080
CMD ["/bin/sh", "-e", "/usr/local/bin/run"]

