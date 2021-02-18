FROM openjdk:13

MAINTAINER David Monschein

ADD start.sh /start.sh 

# Basic installations
RUN \
	yum -y update &&\
	yum -y install unzip && \
	yum -y install nano && \
	yum -y install git && \
	yum -y install curl wget
	
RUN yum -y update && yum -y install dos2unix && \
	dos2unix /start.sh
RUN yum -y install bash
	
#Make the scripts executable 
RUN chmod +x /*.sh

# add JAR
RUN mkdir /dmodel
ADD cipm.consistency-Bundled.jar /dmodel/dmodel.jar
ADD config.yml /dmodel/config.yml
ADD models /dmodel/models
ADD cocome /dmodel/cocome
	
EXPOSE 8080 8080

ENTRYPOINT ["/start.sh"] 
CMD []