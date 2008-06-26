require "rubygems"
require "buildr"

# Keep this structure to allow the build system to update version numbers.
VERSION_NUMBER = "1.2-SNAPSHOT"
NEXT_VERSION = "1.2"

repositories.remote = [ "http://www.intalio.org/public/maven2", "http://dist.codehaus.org/mule/dependencies/maven2/", "http://repo1.maven.org/maven2" ]

repositories.release_to[:username] ||= "release"
repositories.release_to[:url] ||= "sftp://release.intalio.com/home/release/m2repo"

ACTIVATION = "javax.activation:activation:jar:1.1"
AXIOM = group("axiom-api", "axiom-dom", "axiom-impl", :under=>"org.apache.ws.commons.axiom", :version=>"1.2.5")
AXIS2 = [
  group("axis2-adb", "axis2-xmlbeans", :under=>"org.apache.axis2", :version=>"1.3"),
  "org.apache.axis2:axis2-kernel:jar:1.3i1"
]
JAVAMAIL = "javax.mail:mail:jar:1.4.1"             
LOG4J = [ "log4j:log4j:jar:1.2.15" ]
SLF4J = group(%w{ slf4j-api slf4j-log4j12 jcl104-over-slf4j }, :under=>"org.slf4j", :version=>"1.4.3")
STAX_API = [ "stax:stax-api:jar:1.0" ]
WOODSTOX = [ "woodstox:wstx-asl:jar:3.2.1" ]

LIBS = ACTIVATION, AXIOM, AXIS2, JAVAMAIL, LOG4J, SLF4J, STAX_API, WOODSTOX

desc "Email Web Service"
define "com.intalio.bpms.connectors.email" do
  project.version = VERSION_NUMBER
  project.group = "com.intalio.bpms.connectors"

  compile.options.source = "1.5"
  compile.options.target = "1.5"

  compile.with LIBS

  package(:aar).with :libs => [ ACTIVATION, JAVAMAIL, LOG4J, SLF4J ]
  
  task("send" => [compile]) do
    CLASSPATH = Buildr.artifacts(LIBS.flatten).uniq.join(":") + 
                ":" + project("EmailWS").compile.target.to_s
    #puts "CLASSPATH: #{CLASSPATH}"
    system "java -cp #{CLASSPATH} com.intalio.bpms.examples.email.SendEmail EmailWS.properties test-email.xml"
  end
  
end

