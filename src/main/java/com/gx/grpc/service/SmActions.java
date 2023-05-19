package com.gx.grpc.service;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.statemachine.StateMachine;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SmActions extends StateMachineConfigurerAdapter<String, String> {

	@Autowired
	private GxServiceActions obj;

	@SuppressWarnings("deprecation")
	public Action<String, String> action(String actionName) {
		return new Action<String, String>() {
			public void execute(StateContext<String, String> ctx) {
				try {
					Class<?> cls = GxServiceActions.class;
					//Object obj = cls.newInstance();

					Method method = cls.getMethod(actionName, StateContext.class);
					String sid = ctx.getStateMachine().getExtendedState().get("sid", String.class);
					// sm.getExtendedState().getVariables().put(session_id);
					StateMachine<String, String> sm = ctx.getStateMachine();
					System.err.println("TBR +++++ SM OBJ:" + sm);
					log.info("TBR +++++ SM OBJ:" + sm);
					log.info("SID:{} reflect method invoke {}", sid, actionName);

					String rsp = (String) method.invoke(obj, ctx);
					log.info(".......method response {}", rsp);

				} catch (Exception e) {
					try {
						log.error("TBR ------ SM Exp:{}", e);
						Class<?> cls = GxServiceActions.class;
						Object obj = cls.newInstance();
						Method method = cls.getMethod("defaultMethod", String.class);
						method.invoke(obj, actionName);
					} catch (Exception e1) {
						e1.printStackTrace();
						log.error("Exception occured in action...", e);
					}
				}
			}
		};
	}

}
