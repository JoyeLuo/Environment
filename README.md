[<img src="http://www.cloudscale-project.eu/static/img/logo-CloudScale.png">](http://wwww.cloudscale-project.eu)


The CloudScale Environment is a desktop application that integrates the different tools of the CloudScale project, 
namely the Dynamic and Static Spotters, the Analyzer and the Extractor, while driving the user through the flow of the CloudScale Model.
This desktop application can be installed and used in any personal computer running Java 6+, including Windows, MacOS and Linux.

Bundled versions are available here: http://cloudscale-project.github.io/Environment/

Services
-----------------------------------------
Service | Status | Description
------- | ------ | -----------
Travis | [![Build Status](https://travis-ci.org/CloudScale-Project/Environment.svg?branch=master)](https://travis-ci.org/CloudScale-Project/Environment) | Mvn compile
Codacy |  [![Codacy Badge](https://www.codacy.com/project/badge/f08dd5ce953b4a35a7d01e93505365f1)](https://www.codacy.com/app/jure-polutnik/Environment) | Code Quality
Jenkins | Official CI | http://cloudscale.xlab.si/jenkins/job/eu.cloudscaleproject.env/

Build and Run CloudScale Environment
-----------------------------------------

1. Clone the repository.
	* `$ git clone https://github.com/CloudScale-Project/Environment.git`
2. Build Cloudscale Environemnt.
	* `$ mvn package`
3. Run Linux,MacOS,Windows distribution.
	* Bundle location : plugins/eu.cloudscaleproject.env.master/target/products

Develop CloudScale Environment
-----------------------------------------

1. Download and install [Eclipse Luna for RCP and RAP][1]
2. Download and install Eclipse plugin dependencies for CloudScale developement.
	- Go to Eclipse->Help->Install New Software
	- Add CloudScale Toolchain update site: "http://cloudscale.xlab.si/cse/updatesites/toolchain/nightly/".
	- Install CloudScale Dependencies and than Toolchain features (Analyser, Extractor, Static Spotter and Dynamic Spotter).
3. Clone repository
	$ git clone https://github.com/CloudScale-Project/Environment.git
4. Import CloudScale Environment plugins, under "plugins/" directory, into the workbench.

CloudScale Tool-chain
-----------------------------------------
 **Analyser** is used for modelling and analysing the scalability of basic and composed cloud services. It is based on the Palladio; a software architecture simulation framework, which analyses software at the model level for performance bottlenecks, scalability issues and reliability threats, and allows for a subsequent optimisation. In the CloudScale Environment mainly the backend engines will be used to analyse the user’s application. This will be achieved by automatically transforming ScaleDL Overview models into the Palladio Component Models; structure on which Palladio operates. http://www.palladio-simulator.com/tools/
 
 **Extractor** is responsible for extracting scalability models from the system’s source code. It is based on the SoMoX, which provides clustering-based architecture reconstruction to recover the architecture of a software system from source code. The clustering mechanism extracts a software architecture based on source code metrics and construct PCM models to be used by Analyser. https://sdqweb.ipd.kit.edu/wiki/SoMoX
 
 **Static Spotter**  and  **Dynamic Spotter** semi-automatically spots scalability issues in software code. It combines static code analysis to identify structures of scalability anti-patterns, together with a spotting by measurement method (Spotting by Measuring) to spot scalability bottlenecks on the running system. https://code.google.com/p/reclipse-emf/ & http://sopeco.github.io/DynamicSpotter/
 

Plugin descriptions
-----------------------------------------

#### CloudScale Toolchain integration plugins:

- **eu.cloudscaleproject.env.analyser** : 
 The Analyser plugin wraps all external dependencies for the Analyser tool and provides functionality to model, configure and simulate services deployed on the elastic infrastructures.
It mostly contains classes which provide GUI components for the Dashboard editor and the 'validators', that monitor current project input/run/result contents.

- **eu.cloudscaleproject.env.extractor** : 
 The Extractor plugin that wraps all external dependencies for the Extractor tool. It mostly contains classes which provide GUI components for the Dashboard editor and various validators, that monitor current input/run/result configurations.


- **eu.cloudscaleproject.env.spotter** : 
 The Dynamic Spotter plugin that wraps all external dependencies for the Dynamic Spotter tool and provides functionality to analyse and discover performance problems. Dynamic spotter approach is measurement-based, in contrast to the Analyser or Static spotter tool approach.
It mostly contains classes which provide GUI components for the Dashboard editor and various validators, that monitor current input/run/result configurations.

- **eu.cloudscaleproject.env.staticspotter** : 
 Static Spotter plugin that wraps all external dependencies for the Static Spotter tool. It mostly contains classes which provide GUI components for the Dashboard editor and various validators, that monitor current input/run/result configurations.

#### CloudScale application plugins: 

- **eu.cloudscaleproject.env.master** : 
 Plugin used as a base project plugin for building the RCP application. It contains the '.product' file.

- **eu.cloudscaleproject.env.product** : 
 Plugin serves as a main CloudScale application configuration. It contains Eclipse E4 application model, toolbar and menu items, project nature, new project wizard and custom application brandings.

- **eu.cloudscaleproject.env.common** : 
 The Common plugin contains generally used dialogs and custom GUI components, project explorer common operations, functionality to retrieve project files, interfaces for notification and tool status mechanism and common context used by dependency injection. It also contains helper classes for GUI resource management, base class used by extension points to support dependency injection, color manipulator and converter and others. It is required by most of the other plugins. 

- **eu.cloudscaleproject.env.help** : 
 Plugin includes HTML help pages.

#### Plugins providing enhanced integration between tools:

- **eu.cloudscaleproject.env.toolchain** : 
 Toolchain plugin serves as the base plugin for the Dashboard editor. Dashboard editor is the common editor for all the tools that consist CloudScale Environment. It is extended, using the extension point functionality, by the aforementioned Analyser, Dynamic Spotter, Static Spotter and Extractor plugins, to provide GUI components for editing configurations and displaying the results.

- **eu.cloudscaleproject.env.csm2pcm** : 
 Contains QVTO scripts and support classes that gives support for transformation from Overview model to PCM model. PCM model is needed as an input to the Analyser.

 ##### CloudScale Method plugins:

 - **eu.cloudscaleproject.env.method.common** : 
  Common plugin for the Workflow diagram. Workflow diagram shows notifications and enabled options, based on the current state of the project. It contains Method meta-model and Graphiti diagram patterns, used for persisting, viewing and editing Workflow diagram.

 - **eu.cloudscaleproject.env.method.editor** : 
  Plugin that is used as a Eclipse extension to create and edit Workflow diagrams, based on Method meta-model.
This plugin is not meant to be used inside CloudScale product application.
Please read description of the "eu.cloudscaleproject.env.method.common" plugin.

 - **eu.cloudscaleproject.env.method.viewer** : 
  Plugin that provides only view capabilities and dynamic decorations for the Workflow diagram.
Please read description of the "eu.cloudscaleproject.env.method.common" plugin.

#### ScaleDL Overview model plugins (definition, editors, diagrams):

- **org.scaledl.overview** : 
 This plugin contains EMF auto-generated classes, produced from Overview meta-model. It also introduces two new extension points to support different cloud environment specifications and model-to-model transformations.

- **org.scaledl.overview.diagram** : 
 Plugin provides diagram representation of the Overview model. It uses Graphiti framework for displaying and editing EMF based Overview model, together with custom service editors.

- **org.scaledl.overview.edit** : 
 This plugin contains EMF auto-generated "item provider" classes, used mainly by the "org.scaledl.overview.editor" plugin.

- **org.scaledl.overview.editor** : 
 Provides basic EMF editor for the Overview model.

- **org.scaledl.overview.generic** : 
 Overview generic plugin contains different cloud infrastructure specifications for the Overview model. Currently AWS, OpenStack, SAP Hana and Generic cloud infrastructures are supported.

- **org.scaledl.overview.properties** : 
 Overview properties plugin provides extension for the Overview diagram properties panel. It contains custom table and cell editors that facilitates Overview diagram editing.

Feature descriptions
-----------------------------------------

##### Features
- eu.cloudscaleproject.feature.analyser
- eu.cloudscaleproject.feature.dynamicspotter
- eu.cloudscaleproject.feature.environment
- eu.cloudscaleproject.feature.environment.dev
- eu.cloudscaleproject.feature.extractor
- eu.cloudscaleproject.feature.staticspotter


##### Update-site
- eu.cloudscaleproject.updatesite.environment
- eu.cloudscaleproject.updatesite.toolchain

[1]: https://www.eclipse.org/downloads/packages/eclipse-rcp-and-rap-developers/lunasr1
