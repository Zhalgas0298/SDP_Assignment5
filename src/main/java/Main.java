
import java.util.List;

public class Main {

    public static void main(String[] args) {

        Location astana = new Location(
                "Astana", 51.1694, 71.4491
        );

        Location airport = new Location(
                "Airport", 51.0222, 71.4669
        );

        List<Route> routes = List.of(
                new Route("R1", astana, airport,
                        15, 20, 5000, 3.0, true, 3),

                new Route("R2", astana, airport,
                        20, 25, 2500, 2.0, true, 1),

                new Route("R3", astana, airport,
                        12, 28, 3500, 1.0, true, 2),

                new Route("R4", astana, airport,
                        18, 32, 2000, 2.5, true, 1),

                new Route("R5", astana, airport,
                        10, 38, 4000, 1.5, true, 2),

                new Route("R6", astana, airport,
                        25, 18, 6500, 4.0, true, 3),

                new Route("R7", astana, airport,
                        14, 30, 3000, 1.8, false, 1),

                new Route("R8", astana, airport,
                        16, 26, 4500, 2.2, true, 2)
        );

        StrategySelector selector = new StrategySelector();

        DeliveryRequest[] requests = {
                new DeliveryRequest(
                        DeliveryPriority.URGENT, 7000, 40, false
                ),
                new DeliveryRequest(
                        DeliveryPriority.ECONOMY, 5000, 50, false
                ),
                new DeliveryRequest(
                        DeliveryPriority.ECO, 6000, 45, true
                ),
                new DeliveryRequest(
                        DeliveryPriority.STANDARD, 6000, 40, false
                )
        };

        RoutePlanner planner = new RoutePlanner(
                new FastestRouteStrategy()
        );

        for (DeliveryRequest request : requests) {

            RouteStrategy strategy =
                    selector.selectStrategy(request);

            planner.setStrategy(strategy);

            Route selected = planner.plan(routes, request);

            System.out.println(
                    "Priority: " + request.priority()
            );

            System.out.println(
                    "Strategy: " + planner.getCurrentStrategyName()
            );

            System.out.println(
                    "Selected Route: " + selected.id()
            );

            System.out.println("-----------------------");
        }

        planner.setStrategy(new DistanceFirstRouteStrategy());

        Route shortest = planner.plan(routes, requests[3]);

        System.out.println("Fifth Strategy: "
                + planner.getCurrentStrategyName());

        System.out.println("Selected Route: " + shortest.id());


        System.out.println();

        DeliveryRequest comparisonRequest =
                new DeliveryRequest(
                        DeliveryPriority.STANDARD,
                        6000,
                        50,
                        false
                );

        StrategyComparison.compare(
                routes,
                comparisonRequest
        );

        RouteStrategy comparisonStrategy =
                new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2);

        Route selected = comparisonStrategy.selectRoute(
                routes,
                comparisonRequest
        );

        System.out.println();

        RouteExplanation.explain(
                selected,
                routes,
                comparisonRequest
        );

    }


}
