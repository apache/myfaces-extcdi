Release Notes - MyFaces CODI - Version 1.0.4

** Bug
    * [EXTCDI-259] - faces wrappers #getWrapped should return the wrapped instance itself
    * [EXTCDI-266] - WindowContextManagerObserver causes an Exception when redirecting if the requested view does not exists

** Improvement

    * [EXTCDI-249] - move PropertyExpressionInterpreter and SystemPropertyExpressionInterpreter to core-api
    * [EXTCDI-258] - @TransactionScoped outside of a request
    * [EXTCDI-260] - bypass changed behaviour of some versions of mojarra2
    * [EXTCDI-261] - @Secured on stereotypes
    * [EXTCDI-263] - prevent direct usage of ProjectStage.class in @ProjectStageActivated
    * [EXTCDI-265] - implementation validation for @TransactionScoped

** New Feature

    * [EXTCDI-262] - custom meta-data for @Secured
    * [EXTCDI-264] - AbstractMessage optional jpa support

** Task

    * [EXTCDI-267] - preparations for v1.0.4