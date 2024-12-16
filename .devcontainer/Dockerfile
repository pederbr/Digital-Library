FROM mcr.microsoft.com/vscode/devcontainers/java:17

# Install Maven
RUN curl -fsSL https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz -o apache-maven.tar.gz \
    && tar xzf apache-maven.tar.gz -C /usr/local/ \
    && mv /usr/local/apache-maven-3.9.6 /usr/local/maven \
    && rm apache-maven.tar.gz
ENV PATH="/usr/local/maven/bin:${PATH}"

# Install OpenGL and X11 dependencies
RUN apt-get update && apt-get install -y \
    libx11-6 \
    libxext6 \
    libxi6 \
    libxrender1 \
    libxtst6 \
    libfreetype6 \
    libgl1 \
    libgtk-3-0 \
    xvfb \
    mesa-utils \
    libgl1-mesa-glx \
    libgl1-mesa-dri \
    libglu1-mesa \
    x11-xserver-utils \
    && rm -rf /var/lib/apt/lists/*

# Install dependencies for packaging javafx application
RUN apt-get update && apt-get install -y fakeroot dpkg-dev binutils rpm build-essential

# Install Node.js and npm
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
    && apt-get update \
    && apt-get install -y nodejs \
    && npm install -g npm@latest \
    && rm -rf /var/lib/apt/lists/*

# Install Google Cloud CLI
RUN echo "deb [signed-by=/usr/share/keyrings/cloud.google.gpg] http://packages.cloud.google.com/apt cloud-sdk main" | tee -a /etc/apt/sources.list.d/google-cloud-sdk.list \
    && curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key --keyring /usr/share/keyrings/cloud.google.gpg add - \
    && apt-get update && apt-get install -y google-cloud-sdk \
    && rm -rf /var/lib/apt/lists/*

USER vscode