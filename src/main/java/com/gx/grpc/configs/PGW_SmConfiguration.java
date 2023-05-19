package com.gx.grpc.configs;

import java.io.FileReader;
import java.io.Reader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.configurers.StateConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import com.gx.grpc.service.ProcessCCA;
import com.gx.grpc.service.SmActions;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableStateMachineFactory
public class PGW_SmConfiguration extends StateMachineConfigurerAdapter<String, String> {

	private List<SmConfigurations> list = new ArrayList<>();
	@Autowired
	SmActions smActions;

	@Override
	public void configure(StateMachineTransitionConfigurer<String, String> transitions) throws Exception {

//		Reader reader = new FileReader("/home/vsrule/gxconfigs/spr_validate_sm.csv");
		Reader reader = new FileReader("C:\\Users\\sathvikb\\Desktop\\DivyaMam\\pgw_sm.csv");
		CsvToBean<SmConfigurations> csvToBean = new CsvToBeanBuilder<SmConfigurations>(reader)
				.withType(SmConfigurations.class).withIgnoreLeadingWhiteSpace(true).build();
		list = csvToBean.parse();
		for (int i = 0; i < list.size(); i++) {
			StateMachineTransitionConfigurer<String, String> xxx = transitions.withExternal()
					.source(list.get(i).getState()).target(list.get(i).getNextState()).event(list.get(i).getEvent())
					.and();
			transitions = xxx;
		}

	}

	@Override
	public void configure(StateMachineStateConfigurer<String, String> states) throws Exception {
		StateConfigurer<String, String> initial = states.withStates().initial(list.get(0).getState());
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getId().equals("SM"))
				initial = initial.state(list.get(i).getNextState(), smActions.action(list.get(i).getAction()));
			log.info("State:{} Action:{}", list.get(i).getNextState(), list.get(i).getAction());
		}
		initial.end(list.get(list.size() - 1).getNextState());
	}

	@Override
	public void configure(StateMachineConfigurationConfigurer<String, String> config) throws Exception {
		StateMachineListenerAdapter<String, String> adapter = new StateMachineListenerAdapter<String, String>() {

			String smId = null;

			@Override
			public void stateContext(StateContext<String, String> stateContext) {
				smId = stateContext.getStateMachine().getId();
			}

			@Override
			public void stateChanged(State<String, String> from, State<String, String> to) {
				log.info(Instant.now() + " SMID:" + smId + " STATE-CHANGE \nFROM:" + from + "\nTO  :" + to);
			}

		};

		config.withConfiguration().autoStartup(true).listener(adapter);

	}

}
