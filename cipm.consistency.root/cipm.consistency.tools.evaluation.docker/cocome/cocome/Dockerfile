FROM ubuntu:16.04

MAINTAINER Niko Benkler and Tobias Haßberg

# Set environment variables
ENV MD5=4e7ce65489347960e9797d2161e0ada2 \
    JAVA_HOME=/usr/lib/jvm/jdk1.8.0_211 \
	PASSWORD=password

ADD startdomain.sh /startdomain.sh 
ADD stopdomain.sh /stopdomain.sh  
ADD restart.sh /restart.sh
ADD pullChanges.sh /pullChanges.sh
ADD flushkieker.sh /flushkieker.sh

RUN \
	apt-get update &&\
	apt-get install -y software-properties-common && \
	apt-get install unzip && \
	apt-get install nano && \
	apt-get install curl wget && \
	add-apt-repository -y ppa:linuxuprising/java && \
	apt-get update && \
	# echo oracle-java12-installer shared/accepted-oracle-license-v1-2 select true | /usr/bin/debconf-set-selections && \
	# apt-get install -y oracle-java12-installer && \ 
	# apt-get install -y oracle-java12-set-default && \
	apt-get install -y python-software-properties unzip ant git python-virtualenv maven && \
	rm -rf /var/lib/apt/lists/*

# Install Java.
ADD jdk-8u211-linux-x64.tar.gz /jdk

RUN mkdir -p /usr/lib/jvm && \
	mv /jdk/jdk1.8.0_211 /usr/lib/jvm/

RUN update-alternatives --install "/usr/bin/java" "java" "/usr/lib/jvm/jdk1.8.0_211/bin/java" 1 && \
	update-alternatives --install "/usr/bin/javac" "javac" "/usr/lib/jvm/jdk1.8.0_211/bin/javac" 1 && \
	update-alternatives --install "/usr/bin/javaws" "javaws" "/usr/lib/jvm/jdk1.8.0_211/bin/javaws" 1

RUN chmod a+x /usr/bin/java && \
chmod a+x /usr/bin/javac && \
chmod a+x /usr/bin/javaws && \
chown -R root:root /usr/lib/jvm/jdk1.8.0_211 && \
update-alternatives --config java && \
echo 2 && \
update-alternatives --config javac && \
update-alternatives --config javaws && \
update-alternatives --config java && \
export JAVA_HOME=/usr/lib/jvm/jdk1.8.0_211

RUN git clone https://github.com/cocome-community-case-study/cocome-cloud-jee-docker.git usr/src/ && \
 	rm usr/src/*.md  && \
	rm -r usr/src/docker && \
	rm usr/src/glassfish/README.md && \
	mkdir -p /etc/monitoring/ && \
	mkdir -p /etc/sigar/

# Derby bug fix
RUN curl -o db-derby.zip https://mirror.dkd.de/apache//db/derby/db-derby-10.14.2.0/db-derby-10.14.2.0-bin.zip
RUN unzip db-derby.zip -d javadb
RUN rm -rf usr/src/glassfish/glassfish4/javadb && \
	mv javadb/db-derby-10.14.2.0-bin/ usr/src/glassfish/glassfish4/javadb/

ADD cocome-maven-project-setting.xml /usr/src/cocome-maven-project-setting.xml
ADD serviceadapter-settings.xml /usr/src/serviceadapter-settings.xml

#changeFromat and uninstall dos2unix
RUN apt-get update && apt-get install -y dos2unix &&\
	dos2unix /startdomain.sh && \
    dos2unix /restart.sh && \
    dos2unix /pullChanges.sh && \
    dos2unix /flushkieker.sh && \
    dos2unix /stopdomain.sh && apt-get --purge remove -y dos2unix && rm -rf /var/lib/apt/lists/

#Make the scripts executable 
RUN chmod +x /*.sh

#Install sigar
ADD hyperic-sigar-1.6.4.zip hyperic-sigar-1.6.4.zip
RUN unzip hyperic-sigar-1.6.4.zip -d hyperic-sigar-1.6.4 && \
	cd hyperic-sigar-1.6.4/hyperic-sigar-1.6.4 && \
	cp sigar-bin/lib/libsigar-amd64-linux.so /usr/lib

# 4848 (administration), 8080 (HTTP listener), 8181 (HTTPS listener)
EXPOSE 8048 8080 8081 8148 8180 8181 8248 8280 8281 8348 8380 8381 8448 8480 8481

CMD ["/startdomain.sh"]
