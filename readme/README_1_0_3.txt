Release Notes - MyFaces CODI - Version 1.0.3

** Bug
    * [EXTCDI-246] - windowhandler.js fails to correctly chain jsf.util.chain
    * [EXTCDI-247] - windowhandler.js has globally scoped functions and does not chain window.onload
    * [EXTCDI-252] - bean-lookup during the bootstrapping process of the cdi container
    * [EXTCDI-253] - ELException: java.lang.NullPointerException: bean parameter can not be null

** Improvement
    * [EXTCDI-248] - windowhandler.js minor styleguide breaches and coding issue fixes
    * [EXTCDI-250] - cleanup of BeanManager entry in BeanManagerProvider on BeforeShutdown event
    * [EXTCDI-255] - decouple ProjectStageProducer from JsfProjectStageProducer
    * [EXTCDI-256] - [PERF] Avoid unnecessary AbstractList$Itr instances

New Feature
    * [EXTCDI-237] - java-ee5 support modules
    * [EXTCDI-254] - support entity-manager injected via @PersistenceContext

Task
    * [EXTCDI-257] - preparations for v1.0.3
