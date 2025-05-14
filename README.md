<div id="top"></div>

<br />
<div align="center">
  <a href="https://github.com/nathros/lto-manager/">
    <img src="https://raw.githubusercontent.com/nathros/lto-manager/main/src/main/resources/lto/manager/web/assets/img/favico.svg" alt="Logo" width="256">
  </a>

<h3 align="center">LTO Manager</h3>

  <p align="center">
    Simple LTO tape manager for single tape drive
    <br />
    <a href="https://github.com/nathros/lto-manager"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <!--<a href="https://github.com/nathros/lto-manager">View Demo</a>
    ·-->
    <a href="https://github.com/nathros/lto-manager/issues">Report Bug</a>
    ·
    <a href="https://github.com/nathros/lto-manager/issues">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

<!-- [![Product Name Screen Shot][product-screenshot]](https://example.com) -->

This tool is intended for managing a single tape drive.

### Built With

* [![Java][java-17]][java-17-url]
* [![Java][gradle]][gradle-url]

## Java Libraries
* HtmlFlow: https://htmlflow.org/
* SqlBuilder: https://openhms.sourceforge.io/sqlbuilder/
* Embedded SQLite Database: https://github.com/xerial/sqlite-jdbc

## Getting Started
This is an example of how you may give instructions on setting up your project locally.
To get a local copy up and running follow these simple example steps.

### Prerequisites
* Java 17 LTS recommended (should build with 21)
* Gradle 7.x (8.x builds successfully with warnings)
* LTFS

### Linux Dependencies
<details>
<summary><u><b>Expand</b></u></summary>

### Install Java
These commands apply to Debian and derivatives such as Ubuntu, your own system maybe different
```sh
sudo apt-get update
sudo apt-get install openjdk-17-jdk openjdk-17-jre
```

### Install LTFS
Make sure your tape drive is visible, if this returns nothing then check cables / system configuration
```sh
ls /dev/ | grep nst
```
Compile and install: https://github.com/LinearTapeFileSystem/ltfs

Ubuntu 18.04 and 20.04
```sh
sudo apt-get update
sudo apt-get install -y libicu-dev libfuse-dev libxml2-dev uuid-dev libperl-dev libsnmp-perl
sudo apt-get install -y snapd
sudo snap install -y net-snmp

sudo mkdir /opt/ltfs
sudo chown $(whoami) /opt/ltfs
cd /opt/ltfs
git clone https://github.com/LinearTapeFileSystem/ltfs
mv ltfs/{.[!.],}* /opt/ltfs/

./autogen.sh
./configure
make
sudo make install
sudo ldconfig

ltfs # test command
```
<!--
#if you run ltfs now it will complain that libraries are missing - the fix
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/opt/ltfs/src/libltfs/.libs/
echo export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/opt/ltfs/src/libltfs/.libs/ >> ~/.bashrc

sudo apt install make automake autoconf libtool fuse uuid libxml2 libxml2-dev libsnmp-dev pkg-config libfuse-dev uuid-dev icu-devtools

ubuntu 22.04
./autogen.sh
./configure --disable-dependency-tracking
make
sudo make install
sudo ldconfig -v
-->

### Install Gradle
```sh
sudo ./helpers/install-gradle.sh
```
This will install the latest release from version 7
</details>
<hr>

<!--### Windows Dependencies
<details>
<summary><u><b>Expand</b></u></summary>

### Install Java
Download and install: https://download.oracle.com/java/17/archive/jdk-17.0.4_windows-x64_bin.exe

Run CMD as Admin
```sh
setx /M PATH "%PATH%;C:\Program Files\Java\jdk-17.0_4\bin"
 ```

or

In <b>File Explorer</b> right-click on the `This PC` (or `Computer`) icon, then click `Properties` -> `Advanced System Settings` -> `Environmental Variables`.

Under `System Variables` select Path, then click Edit. Add an entry for `C:\Program Files\Java\jdk-17.0_4\bin`. Click OK to save.

### Install LTFS
Download and install: https://www.quantum.com/en/service-support/downloads-and-firmware/ltfs/

### Install Gradle (Build Only)
Download: https://services.gradle.org/distributions/gradle-7.2-bin.zip

Extract to: `C:\Gradle`

Run CMD as Admin
```sh
setx /M PATH "%PATH%;C:\Gradle\bin"
 ```
or

Add `C:\Gradle\bin` to `Environmental Variables`
</details>
<hr>
-->


### Build Project
1. Clone the repo
   ```sh
   git clone https://github.com/nathros/lto-manager
   ```
2. Build
   ```sh
   cd lto-manager
   gradle shadowJar
   # Or if you used the install script: helpers/install-gradle.sh
   gradle7.xx shadowJar # Replace xx with installed version
   ```
<!--<details>
<summary>Optional build parameters</summary>
  Minify CSS and JavaScript

  ```sh
  gradle shadowJarMinify
  ```
</details>-->


3. Compiled Jar will be named `openltom-VERSION.jar` in directory `build`
   Note: VERSION will be `Dev-Build` for untagged commit

## Usage

Run
```sh
java -jar lto-manager-all.jar web httpport 9000
```
Open in browser: http://localhost:9000/

Optional Enable HTTPS
1. Generate Keys
```sh
./config/generate-key.sh
```
On Windows double click `config/generate-key.bat`

  This will ask for a password, 2 files will be created `config/key.keystore` and `config/keypass.config`

2. Run
```sh
java -jar lto-manager-all.jar web httpport 9000 httpsport 9001 keystorepath config/key.keystore keystoreconfigpath config/keypass.config
```
Open in browser: https://localhost:9001/, accept self signed certificate


  <details>
    <summary><u><b>Options in Detail</b></u></summary>
    <!-- have to be followed by an empty line! -->

These are all available launch parameters

* <b>HTTP port</b>
    * Port number for HTTP

      ```sh
      httpport [port]
      ```
    If HTTPS is enabled requests to this port this will redirected to HTTPS

    Example: `httpport 9000`

* <b>HTTPS port</b>
   * Port number for HTTPS

     ```sh
     httpport [port]
     ```
   HTTP requests will be redirected to HTTPS

   If this is specified then options `keystorepath` and `keystoreconfigpath` must also be configured

   Example: `httpports 9001`

* <b>Key store path</b>
   * Path for Key store

     ```sh
     keystorepath [path]
     ```
  Make sure to run `config/generate-key.sh` once to generate a key

   Example: `keystorepath config/key.keystore`

* <b>Key store config path</b>
  * Path for Key store config

    ```sh
    keystoreconfigpath [path]
    ```
  Make sure to run `config/generate-key.sh` once to generate a key

    Example: `keystoreconfigpath config/keypass.config`


* <b>Database path</b>
  * Path to SQLite database file

    ```sh
    dbpath [path]
    ```
  If file it does not exist it will be created

    Note: if this option is not specified then default of `config/base.db` will be used

    Example: `dbpath config/base.db`
  </details>


<!-- ROADMAP -->
## Roadmap

- [ ] (In progress) Virtual filesystem on top of tape library
- [ ] (In progress) Support for Linear Tape File System (LTFS)
- [ ] (In progress) Support for tar
- [ ] (In progress) LTO barcode generator
    - [x] Modify colour schemes and styles
    - [x] Export to HTML
    - [x] Export to SVG
    - [x] Export to PDF
        - [ ] Support easy peel labels
- [ ] Initial release
- [ ] Support for Simple Tape File System (STFS)

See the [open issues](https://github.com/nathros/lto-manager/issues?q=is%3Aissue+is%3Aopen+) for a full list of proposed features (and known issues).

<p align="right">(<a href="#top">back to top</a>)</p>


<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE` for more information.

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- CONTACT -->
## Contact

Open new issue at: [https://github.com/nathros/lto-manager/issues](https://github.com/nathros/lto-manager/issues)

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- ACKNOWLEDGMENTS -->
## Acknowledgments

* []() Acknowledgments

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[java-17]: https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java
[java-17-url]: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
[gradle]: https://img.shields.io/badge/Gradle-7-%2302303a?style=for-the-badge&logo=gradle
[gradle-url]: https://gradle.org/
