import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RouteStrategyTest {

    private final Location start =
            new Location("Astana", 51.1694, 71.4491);

    private final Location end =
            new Location("Airport", 51.0222, 71.4669);

    private final List<Route> routes = List.of(
            new Route("R1", start, end,
                    15, 20, 5000, 3.0, true, 3),
            new Route("R2", start, end,
                    20, 25, 2500, 2.0, true, 1),
            new Route("R3", start, end,
                    12, 28, 3500, 1.0, true, 2),
            new Route("R4", start, end,
                    18, 32, 2000, 2.5, true, 1),
            new Route("R5", start, end,
                    10, 38, 4000, 1.5, true, 2),
            new Route("R6", start, end,
                    25, 18, 6500, 4.0, true, 3),
            new Route("R7", start, end,
                    14, 30, 3000, 1.8, false, 1),
            new Route("R8", start, end,
                    16, 26, 4500, 2.2, true, 2)
    );

    private DeliveryRequest request() {
        return new DeliveryRequest(
                DeliveryPriority.STANDARD, 6000, 50, false
        );
    }

    // 1. Fastest Strategy
    @Test
    void testFastestStrategy() {
        Route result = new FastestRouteStrategy()
                .selectRoute(routes, request());

        assertEquals("R2", result.id());
    }

    // 2. Cheapest Strategy
    @Test
    void testCheapestStrategy() {
        Route result = new CheapestRouteStrategy()
                .selectRoute(routes, request());

        assertEquals("R4", result.id());
    }

    // 3. Eco Strategy
    @Test
    void testEcoStrategy() {
        Route result = new EcoFriendlyRouteStrategy()
                .selectRoute(routes, request());

        assertEquals("R3", result.id());
    }

    // 4. Balanced Strategy
    @Test
    void testBalancedStrategy() {
        Route result = new BalancedRouteStrategy(
                0.4, 0.3, 0.1, 0.2
        ).selectRoute(routes, request());

        assertEquals("R2", result.id());
    }

    // 5. Runtime switching
    @Test
    void testRuntimeSwitching() {
        RoutePlanner planner =
                new RoutePlanner(new CheapestRouteStrategy());

        assertEquals("R4", planner.plan(routes, request()).id());

        planner.setStrategy(new FastestRouteStrategy());

        assertEquals("R2", planner.plan(routes, request()).id());
    }

    // 6. Automatic selection
    @Test
    void testAutomaticSelection() {
        StrategySelector selector = new StrategySelector();

        DeliveryRequest urgent = new DeliveryRequest(
                DeliveryPriority.URGENT, 7000, 50, false
        );

        assertInstanceOf(
                FastestRouteStrategy.class,
                selector.selectStrategy(urgent)
        );
    }

    // 7. Unavailable route
    @Test
    void testUnavailableRoute() {
        DeliveryRequest req = new DeliveryRequest(
                DeliveryPriority.STANDARD, 6000, 50, false
        );

        List<Route> valid = RouteFilters.validRoutes(routes, req);

        assertTrue(valid.stream().noneMatch(
                r -> r.id().equals("R7")
        ));
    }

    // 8. Budget restriction
    @Test
    void testBudgetRestriction() {
        DeliveryRequest req = new DeliveryRequest(
                DeliveryPriority.STANDARD, 3000, 50, false
        );

        Route result = new CheapestRouteStrategy()
                .selectRoute(routes, req);

        assertEquals("R4", result.id());
    }

    // 9. Deadline restriction
    @Test
    void testDeadlineRestriction() {
        DeliveryRequest req = new DeliveryRequest(
                DeliveryPriority.STANDARD, 7000, 30, false
        );

        Route result = new FastestRouteStrategy()
                .selectRoute(routes, req);

        assertEquals("R2", result.id());
    }

    // 10. No valid route
    @Test
    void testNoValidRoute() {
        DeliveryRequest req = new DeliveryRequest(
                DeliveryPriority.STANDARD, 100, 10, false
        );

        assertThrows(
                NoSuitableRouteException.class,
                () -> new FastestRouteStrategy()
                        .selectRoute(routes, req)
        );
    }

    // 11. Invalid Balanced weights
    @Test
    void testInvalidWeights() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BalancedRouteStrategy(
                        0.5, 0.5, 0.5, 0.5
                )
        );
    }

    // 12. Fifth Strategy
    @Test
    void testDistanceFirstStrategy() {
        Route result = new DistanceFirstRouteStrategy()
                .selectRoute(routes, request());

        assertEquals("R5", result.id());
    }

    // 13. Compare two strategies
    @Test
    void testStrategyComparison() {
        Route fastest = new FastestRouteStrategy()
                .selectRoute(routes, request());

        Route cheapest = new CheapestRouteStrategy()
                .selectRoute(routes, request());

        assertNotEquals(fastest.id(), cheapest.id());
    }

    // 14. Boundary case
    @Test
    void testExactBudgetBoundary() {
        DeliveryRequest req = new DeliveryRequest(
                DeliveryPriority.STANDARD, 2900, 30, false
        );

        Route result = new CheapestRouteStrategy()
                .selectRoute(routes, req);

        assertEquals("R2", result.id());
        assertEquals(2900.0,
                RouteCosts.totalCost(result, req));
    }

    // 15. Failure scenario
    @Test
    void testEmptyRouteList() {
        assertThrows(
                NoSuitableRouteException.class,
                () -> new FastestRouteStrategy()
                        .selectRoute(List.of(), request())
        );
    }
}
