Release Notes - MyFaces CODI - Version 1.0.2

** Bug
    * [EXTCDI-220] - @PreDestroy not called when closing sub-groups
    * [EXTCDI-221] - windowId parameter gets added twice on server restart and re-submit of a prev url
    * [EXTCDI-227] - Injection in FacesConverter in composite components
    * [EXTCDI-228] - Navigation with redirect isn't performed in combination with Trinidad 2 support module
    * [EXTCDI-234] - NullPointerException in DefaultWindowContextManager#removeWindowContextIdHolderComponent
    * [EXTCDI-239] - Expire RestScoped beans if it's used in different views
    * [EXTCDI-242] - improve ClientSideWindowHandler windowId passing via cookie

** Improvement
    * [EXTCDI-217] - use BeanManager#getPassivationCapableBean in ViewScopedContext as workaround for weld 
    * [EXTCDI-225] - upgrade to myfaces-parent-10
    * [EXTCDI-229] - Optional SecurityViolationHandler
    * [EXTCDI-243] - add better logging if bean canot be resolved

** New Feature
    * [EXTCDI-171] - allow custom parameters for view-configs
    * [EXTCDI-186] - Trinidad2-support-module
    * [EXTCDI-216] - type-safe navigation for h:link and h:button
    * [EXTCDI-223] - Provide @TransactionScoped Context for @Transactional
    * [EXTCDI-232] - introduce a new RestScope for better GET support
    * [EXTCDI-236] - ConfigurableDataSource
    * [EXTCDI-240] - Enhance ClientSideWindowHandler - remove flickering, etc
    * [EXTCDI-241] - Allow users of the ClientSideWindowHandler to specify if it should get applied per Request

** Task
    * [EXTCDI-226] - upgrade codi to use OWB-1.1.2
    * [EXTCDI-233] - update test dependencies
    * [EXTCDI-238] - cleanup javadoc
    * [EXTCDI-244] - preparations for v1.0.2
