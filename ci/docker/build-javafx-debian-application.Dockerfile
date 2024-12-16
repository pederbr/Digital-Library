FROM ahallemberg/itp:javafx-client-base

RUN apt-get update -y
RUN apt-get install -y fakeroot
RUN apt-get install -y dpkg-dev
RUN apt-get install -y binutils
RUN apt-get install -y rpm
RUN apt-get install -y build-essential