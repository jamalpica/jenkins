#!groovy

import jenkins.*
import jenkins.model.*
import hudson.model.*
import hudson.triggers.SCMTrigger
import hudson.plugins.git.GitSCM
import hudson.plugins.git.BranchSpec
import com.cloudbees.plugins.credentials.domains.Domain
import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.SystemCredentialsProvider
import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey
import javaposse.jobdsl.plugin.*

//import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl
//import hudson.security.csrf.DefaultCrumbIssuer

jenkins = Jenkins.instance
jenkins.save()

dslSecurity = GlobalConfiguration.all().get(GlobalJobDslSecurityConfiguration.class)
dslSecurity.useScriptSecurity = false
dslSecurity.save()

credentialsStore = jenkins.getExtensionList(SystemCredentialsProvider.class)[0];

privateKey = new BasicSSHUserPrivateKey(
		CredentialsScope.GLOBAL,
		"jenkins-key",
		"jenkins",
		new BasicSSHUserPrivateKey.FileOnMasterPrivateKeySource('/opt/keys/jenkins_rsa'),
		"",
		"jenkins ssh key"
)

credentialsStore.store.addCredentials(Domain.global(), privateKey)

dsl_name = "pipelines-dsl-bootstrap"

dsl = new hudson.model.FreeStyleProject(jenkins, dsl_name)
dsl.addTrigger(new SCMTrigger("*/5 * * * *"))
dsl.scm = new GitSCM("https://github.com/jamalpica/jenkins.git")
dsl.scm.branches = [new BranchSpec("*/master")]
dsl.scm.userRemoteConfigs[0].credentialsId = ''

String pipelineScripts = "${dsl_name}.groovy".replaceAll("-", "_")

jobDslBuildStep = new ExecuteDslScripts(
		targets: pipelineScripts,
		usingScriptText: false,
		ignoreExisting: false,
		removedJobAction: RemovedJobAction.DELETE,
		removedViewAction: RemovedViewAction.IGNORE
)

dsl.getBuildersList().add(jobDslBuildStep)

jenkins.add(dsl, dsl_name)


def job1 = jenkins.getItem(dsl.name)

job1.scheduleBuild()

