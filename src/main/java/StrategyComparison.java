import java.util.List;

public class StrategyComparison {

    public static void compare(
            List<Route> routes,
            DeliveryRequest request
    ) {
        List<RouteStrategy> strategies = List.of(
                new FastestRouteStrategy(),
                new CheapestRouteStrategy(),
                new EcoFriendlyRouteStrategy(),
                new BalancedRouteStrategy(
                        0.4, 0.3, 0.1, 0.2
                ),
                new DistanceFirstRouteStrategy()
        );

        System.out.println("=== STRATEGY COMPARISON ===");

        for (RouteStrategy strategy : strategies) {
            try {
                Route route = strategy.selectRoute(routes, request);

                System.out.println(
                        strategy.getStrategyName()
                                + " -> " + route.id()
                                + " | Time: "
                                + (route.estimatedMinutes()
                                + route.trafficLevel() * 5L)
                                + " min"
                                + " | Cost: "
                                + RouteCosts.totalCost(route, request)
                );

            } catch (NoSuitableRouteException e) {
                System.out.println(
                        strategy.getStrategyName()
                                + " -> " + e.getMessage()
                );
            }
        }
    }
}
