package com.gx.grpc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.StateMachine;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SMHandler {
		
	private static StateMachineFactory<String, String> factory;

	public SMHandler(StateMachineFactory<String, String> factory) {
		this.factory = factory;
	}

	/*public SMHandler() {
		
	}*/

	@SuppressWarnings("deprecation")
	public static StateMachine<String, String> CreateSM(String machineId) {
		try {
			System.out.println("51");
			StateMachine<String, String> sm = factory.getStateMachine(machineId);
			System.out.println("51");
			log.info("SMID:{} sm created", machineId);
			sm.start();
			log.info("SMID:{} sm started", machineId);
			return sm;
		} catch (Exception e) {
			log.error("SMID:{} createsm failed Exception:{}", machineId, e.getMessage());
			return null;
		}
	}
	
	public static StateMachine<String, String> ResetSM(String newstate, StateMachine<String, String> machine){
        machine.stop();
        machine.getStateMachineAccessor().
        doWithAllRegions(sma->sma.
        resetStateMachine(new DefaultStateMachineContext<>(newstate, null, null, machine.getExtendedState())));
        log.info("Changed State:{}", machine.getState().toString());
        machine.start();
        return machine;
	}
}
