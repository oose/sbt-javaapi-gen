sbt-javaapi-gen
===============

An sbt plugin to create implicit scala classes to invoke java apis in a more natural scala way.

> This plugin is in an absolute BETA release state
> The API and settings are still subject to change

Usage
=====

Add the plugin to your `projects/plugins.sbt` file:

```scala
    resolvers += "oose releases" at "http://oose.github.io/m2/releases"
    
    addSbtPlugin("oose" % "sbt-javaapi-gen" % "0.3")
```

In your `build.sbt` file or any other `.sbt` file define a set of java classes (not source) which will be inspected by the plugin. For example:

```scala
    def wrapInPackage(packageName: String)(c: String) = packageName + "." + c
    
    javaApiClasses ++=  Set(
      "EngineServices",
      "FormService",
      "HistoryService",
      "IdentityService",
      "ManagementService",
      "ProcessEngine",
      "ProcessEngineInfo",
      "ProcessEngineLifecycleListener",
      "RepositoryService",
      "RuntimeService",
      "TaskService") map wrapInPackage("org.activiti.engine")
```

The sbt setting `javaApiClasses` expects a set of strings of fully qualified java classes. The function `wrapInPackage` is just a convenience if you have many classes from the same package.

After that you are done and can call `compile`. The plugin hooks into the compile process and will generate sources for you. As an alternative you can also call `javaApi` from sbt. Calling `clean` will also delete the generated files.

What it does
============

The plugin tries to find getters and setters in classes or interfaces of java classes and translates them into an idiomatic scala style. These setters and getters are wrapped in an implicit class allowing the programmer to avoid calling chains of x.getFoo() or setThat(newValue). Instead we can write `x.foo` or `x.foo = newValue`.

Currently the following methods are transformed:

* getter with no parameter.
* setter with a single parameter.
* boolean getter of the type isSomething().

The following methods are ignored:

* non public methods
* static methods

The return types of getters are always inferred, so that no type mismatch can happen. If parameter types are complex, the plugin might still produce wrong output. Please file an issue if that happens.

For example this a an sample output (abbreviated):

```scala
    implicit class ScalaTask(val java : Task) {
      def name = java.getName()
      def priority_=(arg0 : Int) = java.setPriority(arg0)
      def priority = java.getPriority()
      def name_=(arg0 : String) = java.setName(arg0)
      def id = java.getId()
      def owner = java.getOwner()
      def description = java.getDescription()
      def processDefinitionId = java.getProcessDefinitionId()
      def executionId = java.getExecutionId()
      def createTime = java.getCreateTime()
      def assignee = java.getAssignee()
      def description_=(arg0 : String) = java.setDescription(arg0)
      def processInstanceId = java.getProcessInstanceId()
      def taskDefinitionKey = java.getTaskDefinitionKey()
      def owner_=(arg0 : String) = java.setOwner(arg0)
      def assignee_=(arg0 : String) = java.setAssignee(arg0)
      def dueDate = java.getDueDate()
      def dueDate_=(arg0 : Date) = java.setDueDate(arg0)
      def processVariables = java.getProcessVariables()
      def delegationState = java.getDelegationState()
      def delegationState_=(arg0 : DelegationState) = java.setDelegationState(arg0)
      def parentTaskId_=(arg0 : String) = java.setParentTaskId(arg0)
      def parentTaskId = java.getParentTaskId()
      def isSuspended = java.isSuspended()
      def taskLocalVariables = java.getTaskLocalVariables()
    }
```

The plugin will also take care of required imports.

Caveats
=======

* The structure of the implicit classes is hardcoded. If your source class is in "x.y.z.MyClass" the plugin will generate "x.y.z.api.ScalaMyClass", where api is an object containing the implicit class.
* Error detection is meagre. Things may and will fail!
* The code is regenerated each time the project is compiled.


