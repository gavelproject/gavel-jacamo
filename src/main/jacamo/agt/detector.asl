+!detect
  <-
  EnabledNormAnnot = norm(id(Id),
						status(enabled),
						activation(Activation),
						issuer(Issuer),
						target(Target),
						deactivation(Deactivation),
						deadline(Deadline),
						content(Content))[H|T];
//  ///////////////////////////
  // get norms whose activation and maintenance condition are believed to be true and ignore norm instances
  .setof(EnabledNormAnnot, 
  	  EnabledNormAnnot
        & not .member(activation_time(_), [H|T])
        & Activation
        & not Deactivation
        & not Deadline,
  	  NormInstances
  );
  for ( .member(EnabledNormAnnot, NormInstances) ) {
      cartago.invoke_obj("java.lang.System",currentTimeMillis,Time);
      .add_annot(EnabledNormAnnot,activation_time(Time), Instance);
      +Instance;
//      !!watch_norm_instance(Instance);
  }.

+!watch_norm_instance(norm(Id,enabled,Condition,Issuer,Content,Sanctions)[activation(T)|Annots])
  : Content =.. [_,obligation,[_,MaintCond,Aim,Deadline],_]
  <-
  .wait(not MaintCond | Aim, Deadline);
  cartago.invoke_obj("java.lang.System",currentTimeMillis,Time);
  Instance = norm(Id,enabled,Condition,Issuer,Content,Sanctions)[activation(T)|Annots];
  
  if (not MaintCond) {
    .add_annot(Instance,deactivation(Time), FinishedInstance);
  } else {
    if (Aim) {
      .add_annot(Instance,fulfillment(Time), FinishedInstance);
    } else {
      .add_annot(Instance,unfulfillment(Time), FinishedInstance);
    }
    +FinishedInstance;
    !report(FinishedInstance);
  }.
  
+!report(FinishedInstance)
  <-
  .print("To do: +!report(FinishedInstance)").