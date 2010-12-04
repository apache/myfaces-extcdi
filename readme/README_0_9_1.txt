Release Notes - MyFaces CODI - Version 0.9.1

** Bug
    * [EXTCDI-78] - CodiUtils must not use internal Context methods to access CDI bean instances
    * [EXTCDI-80] - it isn't possible to provide fine-grained custom config implementations
    * [EXTCDI-83] - InitialRedirect DefaultWindowHandler must encode the original request parameters
    * [EXTCDI-85] - initialRedirectHandler must not redirect if the response is already closed
    * [EXTCDI-87] - ExternalContext.encodeActionUrl() must not be used for URL parameter values

** Improvement
    * [EXTCDI-74] - On Jboss 6 the BeanManagerProvider doesn't return the BeanManager due to classLoader differences
    * [EXTCDI-76] - type-safe navigation for h:link and h:button
    * [EXTCDI-81] - allow customization of the cleanup strategy of empty window contexts
    * [EXTCDI-82] - includeViewParams should be supported by typesafe view configs
    * [EXTCDI-86] - alternative approach for @PageBean

** New Feature
    * [EXTCDI-79] - introduce client side windowhandler
    * [EXTCDI-88] - setup of trinidad support module

** Task
    * [EXTCDI-75] - config - check default values
    * [EXTCDI-77] - Create assembly for binary and source distributions
    * [EXTCDI-84] - check compatibility with trinidad