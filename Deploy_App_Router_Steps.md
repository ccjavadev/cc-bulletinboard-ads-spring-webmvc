***How to deploy your app router***

1. “cd” to a new folder <Your_Folder> and clone the git repo from the link below:

​	git clone https://github.com/ccjavadev/cc-bulletinboard-ads-spring-webmvc.git

2. `git checkout solution-23-Setup-Generic-Authorization`

3. Install **node**

4. Install ***npm***

5. prepare the .npmrc file under the folder src/main/approuter. There is already a file there, so should be no action here.

6. Install the app router node_modules: *npm install --save @sap/approuter*

7. Check the node_modules folder has been generated successfully

8. cf login with your user, and enter your cf org and space

9. Under <Your_Folder>, create the uaa service instance: 

   *`cf cs xsuaa application uaa-bulletinboard -c security/xs-security.json`*

10. Check it has been  successfully created: 

    `cf s | grep uaa`

11. Create the application-logs service instance for ELK:

    `cf cs application-logs lite applogs-bulletinboard`

12. Update the user id in the manifest.yml. For example, replace my id i314100 to yours

13. cf push

14. Next, have a look at the XS UAA service connection information that is part of the `VCAP_SERVICES` environment variable and try to find the `identityzone`:

```
$   cf env approuter
```

> **Note:** The value of `identityzone` (e.g. d012345trial) matches the value of `subdomain` of the subaccount and represents the name of the **tenant** for the next steps.

15. Add the route for the paas tenant:

​    *`cf map-route approuter cfapps.eu10.hana.ondemand.com -n i314100trial-approuter-i314100`*

16. Access your app router in browser, for example:

    https://i314100trial-approuter-i314100-springboot.cfapps.eu10.hana.ondemand.com

    You can replace the subdomain  “i314100trial” with  your subaccount which mapped the route in step 15.

17.  You can refer to the postman testcases to test your app router together with your backend service:

    <https://github.com/ccjavadev/cc-bulletinboard-ads-spring-webmvc/tree/solution-23-Setup-Generic-Authorization/postman_testcases>


**[Reference]**
https://github.com/ccjavadev/cc-coursematerial/blob/master/Security/Exercise_22_DeployApplicationRouter.md