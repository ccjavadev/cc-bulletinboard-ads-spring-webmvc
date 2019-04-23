git clone https://github.com/ccjavadev/cc-bulletinboard-ads-spring-webmvc.git

git checkout solution-23-Setup-Generic-Authorization

Install node, npm

prepare the .npmrc file

npm install --save @sap/approuter

Check the node_modules folder has been generated successfully

cf cs xsuaa application uaa-bulletinboard -c security/xs-security.json

cf s | grep uaa

cf cs application-logs lite applogs-bulletinboard

cf push

cf map-route approuter cfapps.eu10.hana.ondemand.com -n i314100trial-approuter-i314100

