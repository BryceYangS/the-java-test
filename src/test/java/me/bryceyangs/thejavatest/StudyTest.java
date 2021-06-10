package me.bryceyangs.thejavatest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import java.time.Duration;
import java.util.function.Supplier;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StudyTest {

	@FastTest
	@DisplayName("스터디 만들기 🚀")
	void create_new_study() {

		Study study = new Study(3);
		assertNotNull(study);

		// 파라미터 순서 : expected , actual
		assertAll(
			() -> 		assertEquals(StudyStatus.DRAFT, study.getStatus(), new Supplier<String>() {
				@Override
				public String get() {
					return "스터디를 처음 만들면 상태값이 DRAFT여야 한다.";
				}
			}),
			() -> assertEquals(StudyStatus.DRAFT, study.getStatus(), () -> "스터디를 처음 만들면 상태값이 DRAFT여야 한다."),
			() -> assertTrue(study.getLimit() > 0, "스터디 최대 참석 가능 인원은 0보다 커야 한다.")
		);
	}

	@SlowTest
	@EnabledIfEnvironmentVariable(named = "TEST_ENV", matches = "LOCAL")
	public void assertThrowsTest() throws Exception {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> new Study(-1));
		assertEquals("limit은 0보다 커야한다", exception.getMessage());
	}

	@Test
	@Tag("fast")
	@DisabledOnOs(OS.MAC)
	public void timeOut() throws Exception {
		// assertTimeout(Duration.ofMillis(100), () -> {
		// 	new Study(10);
		// 	Thread.sleep(300);
		// });

		assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
			new Study(10);
			Thread.sleep(300);
		});
	}

	@Test
	@EnabledIfEnvironmentVariable(named = "TEST_ENV", matches = "LOCAL")
	@EnabledOnOs({OS.MAC, OS.LINUX})
	public void 조건에_따라_테스트_실행() throws Exception {
		String test_env = System.getenv("TEST_ENV");
		System.out.println(test_env);
		assumeTrue("LOCAL".equalsIgnoreCase(test_env));

		assumingThat("LOCAL".equalsIgnoreCase(test_env), () -> {
			Study study = new Study(10);
			assertThat(study.getLimit()).isGreaterThan(0);
		});
	}

	@Test
	public void create1_study() throws Exception {
		System.out.println("create1");
	}

	@DisplayName("반복 테스트")
	@RepeatedTest(value = 10, name = "{displayName} {currentRepetition}/{totalRepetitions}")
	void repeatTest(RepetitionInfo repetitionInfo) throws Exception {
		System.out.println(
			"test " + repetitionInfo.getCurrentRepetition() + "/" + repetitionInfo.getTotalRepetitions());
	}

	@DisplayName("파라미터 테스트")
	@ParameterizedTest(name = "{index}, {displayName}, message={0}")
	@CsvSource({"10, 자바 스터디", "20, 스프링"})
	void parameteriedTest(@AggregateWith(StudyAggregator.class) Study study) throws Exception {
		System.out.println(study);
	}

	static class StudyAggregator implements ArgumentsAggregator {

		@Override
		public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws
			ArgumentsAggregationException {
			return new Study(argumentsAccessor.getInteger(0), argumentsAccessor.getString(1));
		}

	}

	static class StudyConverter extends SimpleArgumentConverter {

		@Override
		protected Object convert(Object o, Class<?> aClass) throws ArgumentConversionException {
			assertEquals(Study.class, aClass, "Can only convert to Study");
			return new Study(Integer.parseInt(o.toString()));
		}
	}

	@BeforeAll
	static void beforeAll() {
		System.out.println("before all");
	}

	@AfterAll
	static void afterAll() {
		System.out.println("after all");
	}

	@BeforeEach
	void beforeEach() {
		System.out.println("before each");
	}

	@AfterEach
	void afterEach() {
		System.out.println("after each");
	}
}