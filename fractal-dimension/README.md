To generate britain.js, in core:

mvn -q compile exec:java \
  -Dexec.mainClass=name.tomwhite.howfarawayisthesea.DumpCoast \
  -Dexec.args="../webapp/src/main/webapp/WEB-INF/data/land.bin" \
  > ../fractal-dimension/britain.js
  
Then edit to remove first line, which is a spurious debug line.

To get a simplified representation, try

mvn -q compile exec:java   -Dexec.mainClass=name.tomwhite.howfarawayisthesea.DumpCoast   -Dexec.args="../webapp/src/main/webapp/WEB-INF/data/land.bin"   > ../fractal-dimension/britain-simplified.js