MyXML
=====

This is a relatively simple XML parser, writer and validator build on top of w3c.dom.

Maven
-----

To import this with Maven please see below. You need to add this Github maven repository to your settings.

	<activeProfiles>
		<activeProfile>github</activeProfile>
	</activeProfiles>
	<profiles>
		<profile>
			<id>github</id>
			<repositories>
				<repository>
					<id>central</id>
					<url>https://repo1.maven.org/maven2</url>
					<releases><enabled>true</enabled></releases>
					<snapshots><enabled>true</enabled></snapshots>
				</repository>
				<repository>
					<id>github</id>
					<name>GitHub Antafes Apache Maven Packages</name>
					<url>https://maven.pkg.github.com/antafes/MyXML</url>
				</repository>
			</repositories>
		</profile>
	</profiles>
	
    <servers>
        <server>
            <id>github</id>
            <username>antafes</username>
            <password>22656007ae5f8538f1e7e71f726af75c9eea5a09</password>
        </server>
    </servers>
    
And in addition add the dependency to the version of the library you want to use.
