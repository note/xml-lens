package pl.msitko.xml.bench

object SomeXml {
  private val _someXml =
    """<?xml version="1.0" encoding="UTF-8"?>
      |
      |<!--
      |Licensed to the Apache Software Foundation (ASF) under one
      |or more contributor license agreements.  See the NOTICE file
      |distributed with this work for additional information
      |regarding copyright ownership.  The ASF licenses this file
      |to you under the Apache License, Version 2.0 (the
      |"License"); you may not use this file except in compliance
      |with the License.  You may obtain a copy of the License at
      |
      |    http://www.apache.org/licenses/LICENSE-2.0
      |
      |Unless required by applicable law or agreed to in writing,
      |software distributed under the License is distributed on an
      |"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
      |KIND, either express or implied.  See the License for the
      |specific language governing permissions and limitations
      |under the License.
      |-->
      |
      |<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
      |  <modelVersion>4.0.0</modelVersion>
      |
      |  <parent>
      |    <groupId>org.apache.maven</groupId>
      |    <artifactId>maven-parent</artifactId>
      |    <version>27</version>
      |    <relativePath>../pom/maven/pom.xml</relativePath>
      |  </parent>
      |
      |  <artifactId>maven</artifactId>
      |  <version>3.5.1-SNAPSHOT</version>
      |  <packaging>pom</packaging>
      |
      |  <name>Apache Maven</name>
      |  <description>Maven is a software build management and
      |    comprehension tool. Based on the concept of a project object model:
      |    builds, dependency management, documentation creation, site
      |    publication, and distribution publication are all controlled from
      |    the declarative file. Maven can be extended by plugins to utilise a
      |    number of other development tools for reporting or the build
      |    process.
      |  </description>
      |  <url>https://maven.apache.org/ref/${project.version}/</url>
      |  <inceptionYear>2001</inceptionYear>
      |
      |  <properties>
      |    <maven.version>3.0.5</maven.version>
      |    <maven.compiler.source>1.7</maven.compiler.source>
      |    <maven.compiler.target>1.7</maven.compiler.target>
      |    <classWorldsVersion>2.5.2</classWorldsVersion>
      |    <commonsCliVersion>1.4</commonsCliVersion>
      |    <commonsLangVersion>3.5</commonsLangVersion>
      |    <junitVersion>4.12</junitVersion>
      |    <mockitoVersion>1.10.19</mockitoVersion>
      |    <plexusVersion>1.7.1</plexusVersion>
      |    <plexusInterpolationVersion>1.24</plexusInterpolationVersion>
      |    <plexusUtilsVersion>3.0.24</plexusUtilsVersion>
      |    <guavaVersion>20.0</guavaVersion>
      |    <guiceVersion>4.0</guiceVersion>
      |    <sisuInjectVersion>0.3.3</sisuInjectVersion>
      |    <wagonVersion>2.12</wagonVersion>
      |    <securityDispatcherVersion>1.4</securityDispatcherVersion>
      |    <cipherVersion>1.7</cipherVersion>
      |    <modelloVersion>1.9.1</modelloVersion>
      |    <jxpathVersion>1.3</jxpathVersion>
      |    <resolverVersion>1.0.3</resolverVersion>
      |    <slf4jVersion>1.7.22</slf4jVersion>
      |    <maven.test.redirectTestOutputToFile>true</maven.test.redirectTestOutputToFile>
      |    <!-- Control the name of the distribution and information output by mvn -->
      |    <distributionId>apache-maven</distributionId>
      |    <distributionShortName>Maven</distributionShortName>
      |    <distributionName>Apache Maven</distributionName>
      |    <maven.site.path>ref/3-LATEST</maven.site.path>
      |    <checkstyle.violation.ignore>RedundantThrows,NewlineAtEndOfFile,ParameterNumber,MethodLength,FileLength,JavadocType,MagicNumber,InnerAssignment,MethodName</checkstyle.violation.ignore>
      |    <checkstyle.excludes>**/package-info.java</checkstyle.excludes>
      |  </properties>
      |
      |  <modules>
      |    <module>maven-plugin-api</module>
      |    <module>maven-builder-support</module>
      |    <module>maven-model</module>
      |    <module>maven-model-builder</module>
      |    <module>maven-core</module>
      |    <module>maven-settings</module>
      |    <module>maven-settings-builder</module>
      |    <module>maven-artifact</module>
      |    <module>maven-resolver-provider</module>
      |    <module>maven-repository-metadata</module>
      |    <module>maven-slf4j-provider</module>
      |    <module>maven-embedder</module>
      |    <module>maven-compat</module>
      |    <module>apache-maven</module>
      |  </modules>
      |
      |  <scm>
      |    <connection>scm:git:https://git-wip-us.apache.org/repos/asf/maven.git</connection>
      |    <developerConnection>scm:git:https://git-wip-us.apache.org/repos/asf/maven.git</developerConnection>
      |    <url>https://github.com/apache/maven/tree/${project.scm.tag}</url>
      |    <tag>master</tag>
      |  </scm>
      |  <issueManagement>
      |    <system>jira</system>
      |    <url>https://issues.apache.org/jira/browse/MNG</url>
      |  </issueManagement>
      |  <ciManagement>
      |    <system>Jenkins</system>
      |    <url>https://builds.apache.org/job/maven-3.x/</url>
      |  </ciManagement>
      |  <distributionManagement>
      |    <downloadUrl>https://maven.apache.org/download.html</downloadUrl>
      |    <site>
      |      <id>apache.website</id>
      |      <url>scm:svn:https://svn.apache.org/repos/infra/websites/production/maven/components/${maven.site.path}</url>
      |    </site>
      |  </distributionManagement>
      |
      |  <contributors>
      |    <contributor>
      |      <name>Stuart McCulloch</name>
      |    </contributor>
      |    <contributor>
      |      <name>Christian Schulte (MNG-2199)</name>
      |    </contributor>
      |    <contributor>
      |      <name>Christopher Tubbs (MNG-4226)</name>
      |    </contributor>
      |    <contributor>
      |      <name>Konstantin Perikov (MNG-4565)</name>
      |    </contributor>
      |    <contributor>
      |      <name>Sébastian Le Merdy (MNG-5613)</name>
      |    </contributor>
      |    <contributor>
      |      <name>Mark Ingram (MNG-5639)</name>
      |    </contributor>
      |    <contributor>
      |      <name>Phil Pratt-Szeliga (MNG-5645)</name>
      |    </contributor>
      |    <contributor>
      |      <name>Florencia Tarditti (PR 41)</name>
      |    </contributor>
      |    <contributor>
      |      <name>Anton Tanasenko</name>
      |    </contributor>
      |    <contributor>
      |      <name>Joseph Walton (MNG-5297)</name>
      |    </contributor>
      |  </contributors>
      |
      |  <!--bootstrap-start-comment-->
      |  <dependencyManagement>
      |    <!--bootstrap-end-comment-->
      |    <dependencies>
      |      <!--  Maven Modules -->
      |      <!--bootstrap-start-comment-->
      |      <dependency>
      |        <groupId>org.apache.maven</groupId>
      |        <artifactId>maven-model</artifactId>
      |        <version>${project.version}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.apache.maven</groupId>
      |        <artifactId>maven-settings</artifactId>
      |        <version>${project.version}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.apache.maven</groupId>
      |        <artifactId>maven-settings-builder</artifactId>
      |        <version>${project.version}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.apache.maven</groupId>
      |        <artifactId>maven-plugin-api</artifactId>
      |        <version>${project.version}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.apache.maven</groupId>
      |        <artifactId>maven-embedder</artifactId>
      |        <version>${project.version}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.apache.maven</groupId>
      |        <artifactId>maven-core</artifactId>
      |        <version>${project.version}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.apache.maven</groupId>
      |        <artifactId>maven-model-builder</artifactId>
      |        <version>${project.version}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.apache.maven</groupId>
      |        <artifactId>maven-compat</artifactId>
      |        <version>${project.version}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.apache.maven</groupId>
      |        <artifactId>maven-artifact</artifactId>
      |        <version>${project.version}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.apache.maven</groupId>
      |        <artifactId>maven-resolver-provider</artifactId>
      |        <version>${project.version}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.apache.maven</groupId>
      |        <artifactId>maven-repository-metadata</artifactId>
      |        <version>${project.version}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.apache.maven</groupId>
      |        <artifactId>maven-builder-support</artifactId>
      |        <version>${project.version}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.apache.maven</groupId>
      |        <artifactId>maven-slf4j-provider</artifactId>
      |        <version>${project.version}</version>
      |      </dependency>
      |      <!--bootstrap-end-comment-->
      |      <!--  Plexus -->
      |      <dependency>
      |        <groupId>org.codehaus.plexus</groupId>
      |        <artifactId>plexus-utils</artifactId>
      |        <version>${plexusUtilsVersion}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>com.google.guava</groupId>
      |        <artifactId>guava</artifactId>
      |        <version>${guavaVersion}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>com.google.inject</groupId>
      |        <artifactId>guice</artifactId>
      |        <version>${guiceVersion}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>com.google.inject</groupId>
      |        <artifactId>guice</artifactId>
      |        <version>${guiceVersion}</version>
      |        <classifier>no_aop</classifier>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.eclipse.sisu</groupId>
      |        <artifactId>org.eclipse.sisu.plexus</artifactId>
      |        <version>${sisuInjectVersion}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.eclipse.sisu</groupId>
      |        <artifactId>org.eclipse.sisu.inject</artifactId>
      |        <version>${sisuInjectVersion}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>javax.inject</groupId>
      |        <artifactId>javax.inject</artifactId>
      |        <version>1</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>javax.annotation</groupId>
      |        <artifactId>jsr250-api</artifactId>
      |        <version>1.0</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.codehaus.plexus</groupId>
      |        <artifactId>plexus-component-annotations</artifactId>
      |        <version>${plexusVersion}</version>
      |        <exclusions>
      |          <exclusion>
      |            <groupId>junit</groupId>
      |            <artifactId>junit</artifactId>
      |          </exclusion>
      |        </exclusions>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.codehaus.plexus</groupId>
      |        <artifactId>plexus-classworlds</artifactId>
      |        <version>${classWorldsVersion}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.codehaus.plexus</groupId>
      |        <artifactId>plexus-interpolation</artifactId>
      |        <version>${plexusInterpolationVersion}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.apache.maven.shared</groupId>
      |        <artifactId>maven-shared-utils</artifactId>
      |        <version>3.1.0</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.fusesource.jansi</groupId>
      |        <artifactId>jansi</artifactId>
      |        <version>1.16</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.slf4j</groupId>
      |        <artifactId>slf4j-api</artifactId>
      |        <version>${slf4jVersion}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.slf4j</groupId>
      |        <artifactId>slf4j-simple</artifactId>
      |        <version>${slf4jVersion}</version>
      |        <optional>true</optional>
      |      </dependency>
      |      <dependency>
      |        <groupId>ch.qos.logback</groupId>
      |        <artifactId>logback-classic</artifactId>
      |        <version>1.2.1</version>
      |        <optional>true</optional>
      |      </dependency>
      |      <!--  Wagon -->
      |      <dependency>
      |        <groupId>org.apache.maven.wagon</groupId>
      |        <artifactId>wagon-provider-api</artifactId>
      |        <version>${wagonVersion}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.apache.maven.wagon</groupId>
      |        <artifactId>wagon-file</artifactId>
      |        <version>${wagonVersion}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.apache.maven.wagon</groupId>
      |        <artifactId>wagon-http</artifactId>
      |        <version>${wagonVersion}</version>
      |        <classifier>shaded</classifier>
      |        <exclusions>
      |          <exclusion>
      |            <groupId>commons-logging</groupId>
      |            <artifactId>commons-logging</artifactId>
      |          </exclusion>
      |        </exclusions>
      |      </dependency>
      |      <!--  Repository -->
      |      <dependency>
      |        <groupId>org.apache.maven.resolver</groupId>
      |        <artifactId>maven-resolver-api</artifactId>
      |        <version>${resolverVersion}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.apache.maven.resolver</groupId>
      |        <artifactId>maven-resolver-spi</artifactId>
      |        <version>${resolverVersion}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.apache.maven.resolver</groupId>
      |        <artifactId>maven-resolver-impl</artifactId>
      |        <version>${resolverVersion}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.apache.maven.resolver</groupId>
      |        <artifactId>maven-resolver-util</artifactId>
      |        <version>${resolverVersion}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.apache.maven.resolver</groupId>
      |        <artifactId>maven-resolver-connector-basic</artifactId>
      |        <version>${resolverVersion}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.apache.maven.resolver</groupId>
      |        <artifactId>maven-resolver-transport-wagon</artifactId>
      |        <version>${resolverVersion}</version>
      |      </dependency>
      |      <!--  Commons -->
      |      <dependency>
      |        <groupId>commons-cli</groupId>
      |        <artifactId>commons-cli</artifactId>
      |        <version>${commonsCliVersion}</version>
      |        <exclusions>
      |          <exclusion>
      |            <groupId>commons-lang</groupId>
      |            <artifactId>commons-lang</artifactId>
      |          </exclusion>
      |          <exclusion>
      |            <groupId>commons-logging</groupId>
      |            <artifactId>commons-logging</artifactId>
      |          </exclusion>
      |        </exclusions>
      |      </dependency>
      |      <dependency>
      |        <groupId>commons-jxpath</groupId>
      |        <artifactId>commons-jxpath</artifactId>
      |        <version>${jxpathVersion}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.apache.commons</groupId>
      |        <artifactId>commons-lang3</artifactId>
      |        <version>${commonsLangVersion}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.sonatype.plexus</groupId>
      |        <artifactId>plexus-sec-dispatcher</artifactId>
      |        <version>${securityDispatcherVersion}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.sonatype.plexus</groupId>
      |        <artifactId>plexus-cipher</artifactId>
      |        <version>${cipherVersion}</version>
      |      </dependency>
      |      <dependency>
      |        <groupId>org.mockito</groupId>
      |        <artifactId>mockito-core</artifactId>
      |        <version>${mockitoVersion}</version>
      |      </dependency>
      |    </dependencies>
      |    <!--bootstrap-start-comment-->
      |  </dependencyManagement>
      |  <!--bootstrap-end-comment-->
      |  <!--bootstrap-start-comment-->
      |  <dependencies>
      |    <dependency>
      |      <groupId>junit</groupId>
      |      <artifactId>junit</artifactId>
      |      <version>${junitVersion}</version>
      |      <scope>test</scope>
      |    </dependency>
      |  </dependencies>
      |  <!--bootstrap-end-comment-->
      |
      |  <build>
      |    <pluginManagement>
      |      <plugins>
      |        <plugin>
      |          <groupId>org.codehaus.plexus</groupId>
      |          <artifactId>plexus-component-metadata</artifactId>
      |          <version>${plexusVersion}</version>
      |          <executions>
      |            <execution>
      |              <goals>
      |                <goal>generate-metadata</goal>
      |                <goal>generate-test-metadata</goal>
      |              </goals>
      |            </execution>
      |          </executions>
      |        </plugin>
      |        <plugin>
      |          <groupId>org.eclipse.sisu</groupId>
      |          <artifactId>sisu-maven-plugin</artifactId>
      |          <version>${sisuInjectVersion}</version>
      |          <executions>
      |            <execution>
      |              <goals>
      |                <goal>main-index</goal>
      |                <goal>test-index</goal>
      |              </goals>
      |            </execution>
      |          </executions>
      |        </plugin>
      |        <plugin>
      |          <groupId>org.apache.maven.plugins</groupId>
      |          <artifactId>maven-release-plugin</artifactId>
      |          <configuration>
      |            <autoVersionSubmodules>true</autoVersionSubmodules>
      |          </configuration>
      |        </plugin>
      |        <plugin>
      |          <groupId>org.apache.maven.plugins</groupId>
      |          <artifactId>maven-surefire-plugin</artifactId>
      |          <configuration>
      |            <argLine>-Xmx256m</argLine>
      |          </configuration>
      |        </plugin>
      |        <plugin>
      |          <groupId>org.codehaus.modello</groupId>
      |          <artifactId>modello-maven-plugin</artifactId>
      |          <version>${modelloVersion}</version>
      |          <executions>
      |            <execution>
      |              <id>modello-site-docs</id>
      |              <phase>pre-site</phase>
      |              <goals>
      |                <goal>xdoc</goal>
      |                <goal>xsd</goal>
      |              </goals>
      |            </execution>
      |            <execution>
      |              <id>modello</id>
      |              <goals>
      |                <goal>java</goal>
      |                <goal>xpp3-reader</goal>
      |                <goal>xpp3-writer</goal>
      |              </goals>
      |            </execution>
      |          </executions>
      |        </plugin>
      |        <plugin>
      |          <groupId>org.apache.felix</groupId>
      |          <artifactId>maven-bundle-plugin</artifactId>
      |          <version>1.0.0</version>
      |        </plugin>
      |        <plugin>
      |          <groupId>org.codehaus.mojo</groupId>
      |          <artifactId>buildnumber-maven-plugin</artifactId>
      |          <version>1.4</version>
      |        </plugin>
      |        <plugin>
      |          <groupId>org.apache.maven.plugins</groupId>
      |          <artifactId>maven-site-plugin</artifactId>
      |          <configuration>
      |            <topSiteURL>scm:svn:https://svn.apache.org/repos/infra/websites/production/maven/components/${maven.site.path}</topSiteURL>
      |          </configuration>
      |        </plugin>
      |        <plugin>
      |          <groupId>org.apache.maven.plugins</groupId>
      |          <artifactId>maven-scm-publish-plugin</artifactId>
      |          <version>1.1</version>
      |        </plugin>
      |        <plugin>
      |          <groupId>org.apache.rat</groupId>
      |          <artifactId>apache-rat-plugin</artifactId>
      |          <configuration>
      |            <excludes>
      |              <exclude>src/test/resources*/**</exclude>
      |              <exclude>src/test/projects/**</exclude>
      |              <exclude>src/test/remote-repo/**</exclude>
      |              <exclude>**/*.odg</exclude>
      |            </excludes>
      |          </configuration>
      |        </plugin>
      |        <!--This plugins configuration is used to store Eclipse m2e settings only. It has no influence on the Maven build itself.-->
      |        <plugin>
      |          <groupId>org.eclipse.m2e</groupId>
      |          <artifactId>lifecycle-mapping</artifactId>
      |          <version>1.0.0</version>
      |          <configuration>
      |            <lifecycleMappingMetadata>
      |              <pluginExecutions>
      |                <pluginExecution>
      |                  <pluginExecutionFilter>
      |                    <groupId>org.apache.rat</groupId>
      |                    <artifactId>apache-rat-plugin</artifactId>
      |                    <versionRange>[0.10,)</versionRange>
      |                    <goals>
      |                      <goal>check</goal>
      |                    </goals>
      |                  </pluginExecutionFilter>
      |                  <action>
      |                    <ignore />
      |                  </action>
      |                </pluginExecution>
      |              </pluginExecutions>
      |            </lifecycleMappingMetadata>
      |          </configuration>
      |        </plugin>
      |        <plugin>
      |          <groupId>org.codehaus.mojo</groupId>
      |          <artifactId>findbugs-maven-plugin</artifactId>
      |          <version>3.0.4</version>
      |        </plugin>
      |        <plugin>
      |          <groupId>org.apache.maven.plugins</groupId>
      |          <artifactId>maven-assembly-plugin</artifactId>
      |          <version>3.0.0</version>
      |        </plugin>
      |      </plugins>
      |    </pluginManagement>
      |    <plugins>
      |      <plugin>
      |        <groupId>org.codehaus.mojo</groupId>
      |        <artifactId>animal-sniffer-maven-plugin</artifactId>
      |        <version>1.15</version>
      |        <configuration>
      |          <signature>
      |            <groupId>org.codehaus.mojo.signature</groupId>
      |            <artifactId>java17</artifactId>
      |            <version>1.0</version>
      |          </signature>
      |        </configuration>
      |        <executions>
      |          <execution>
      |            <id>check-java-compat</id>
      |            <phase>process-classes</phase>
      |            <goals>
      |              <goal>check</goal>
      |            </goals>
      |          </execution>
      |        </executions>
      |      </plugin>
      |      <plugin>
      |        <groupId>org.apache.maven.plugins</groupId>
      |        <artifactId>maven-doap-plugin</artifactId>
      |        <version>1.2</version>
      |        <configuration>
      |          <asfExtOptions>
      |            <charter>The mission of the Apache Maven project is to create and maintain software
      |            libraries that provide a widely-used project build tool, targeting mainly Java
      |            development. Apache Maven promotes the use of dependencies via a
      |            standardized coordinate system, binary plugins, and a standard build
      |            lifecycle.</charter>
      |          </asfExtOptions>
      |        </configuration>
      |      </plugin>
      |      <plugin>
      |        <groupId>org.apache.rat</groupId>
      |        <artifactId>apache-rat-plugin</artifactId>
      |        <configuration>
      |          <excludes combine.children="append">
      |            <exclude>bootstrap/**</exclude>
      |            <exclude>README.bootstrap.txt</exclude>
      |            <exclude>.repository/**</exclude> <!-- jenkins with local maven repository -->
      |            <exclude>.maven/spy.log</exclude> <!-- hudson maven3 integration log -->
      |            <exclude>.java-version</exclude>
      |            <exclude>README.md</exclude>
      |          </excludes>
      |        </configuration>
      |      </plugin>
      |      <plugin>
      |        <groupId>org.apache.maven.plugins</groupId>
      |        <artifactId>maven-enforcer-plugin</artifactId>
      |        <executions>
      |          <execution>
      |            <id>enforce-maven</id>
      |            <goals>
      |              <goal>enforce</goal>
      |            </goals>
      |            <configuration>
      |              <rules>
      |                <requireMavenVersion>
      |                  <version>${maven.version}</version>
      |                </requireMavenVersion>
      |              </rules>
      |            </configuration>
      |          </execution>
      |        </executions>
      |      </plugin>
      |    </plugins>
      |  </build>
      |
      |  <profiles>
      |    <profile>
      |      <id>apache-release</id>
      |      <build>
      |        <plugins>
      |          <plugin>
      |            <artifactId>maven-assembly-plugin</artifactId>
      |            <executions>
      |              <execution>
      |                <id>source-release-assembly</id>
      |                <configuration>
      |                  <!-- we have a dedicated distribution module -->
      |                  <skipAssembly>true</skipAssembly>
      |                </configuration>
      |              </execution>
      |            </executions>
      |          </plugin>
      |        </plugins>
      |      </build>
      |    </profile>
      |    <profile>
      |      <id>reporting</id>
      |      <reporting>
      |        <plugins>
      |          <plugin>
      |            <groupId>org.apache.maven.plugins</groupId>
      |            <artifactId>maven-javadoc-plugin</artifactId>
      |            <configuration>
      |              <!-- TODO Remove when we upgrade to maven-parent 31 -->
      |              <locale>en</locale>
      |              <tags>
      |                <tag>
      |                  <name>provisional</name>
      |                  <placement>tf</placement>
      |                  <head>Provisional:</head>
      |                </tag>
      |              </tags>
      |            </configuration>
      |            <reportSets>
      |              <reportSet>
      |                <id>aggregate</id>
      |                <inherited>false</inherited>
      |                <reports>
      |                  <report>aggregate</report>
      |                </reports>
      |              </reportSet>
      |            </reportSets>
      |          </plugin>
      |          <plugin>
      |            <groupId>org.apache.maven.plugins</groupId>
      |            <artifactId>maven-jxr-plugin</artifactId>
      |            <reportSets>
      |              <reportSet>
      |                <id>aggregate</id>
      |                <inherited>false</inherited>
      |                <reports>
      |                  <report>aggregate</report>
      |                </reports>
      |              </reportSet>
      |            </reportSets>
      |          </plugin>
      |        </plugins>
      |      </reporting>
      |    </profile>
      |    <profile>
      |      <id>maven-repo-local</id>
      |      <activation>
      |        <property>
      |          <name>maven.repo.local</name>
      |        </property>
      |      </activation>
      |      <build>
      |        <plugins>
      |          <plugin>
      |            <groupId>org.apache.maven.plugins</groupId>
      |            <artifactId>maven-surefire-plugin</artifactId>
      |            <configuration>
      |              <systemProperties combine.children="append">
      |                <property>
      |                  <!-- Pass this through to the tests (if set!) to have them pick the right repository -->
      |                  <name>maven.repo.local</name>
      |                  <value>${maven.repo.local}</value>
      |                </property>
      |              </systemProperties>
      |            </configuration>
      |          </plugin>
      |        </plugins>
      |      </build>
      |    </profile>
      |  </profiles>
      |</project>
    """.stripMargin

  def someXml = _someXml
}