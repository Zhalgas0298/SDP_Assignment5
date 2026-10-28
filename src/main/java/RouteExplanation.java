import java.util.List;

public class RouteExplanation {

    public static void explain(
            Route selected,
            List<Route> routes,
            DeliveryRequest request
    ) {
        System.out.println("=== ROUTE EXPLANATION ===");
        System.out.println("Selected: " + selected.id());

        long finalTime = selected.estimatedMinutes()
                + selected.trafficLevel() * 5L;

        System.out.println("Base time: "
                + selected.estimatedMinutes() + " min");

        System.out.println("Traffic penalty: "
                + selected.trafficLevel() * 5L + " min");

        System.out.println("Final time: "
                + finalTime + " min");

        System.out.println("Total cost: "
                + RouteCosts.totalCost(selected, request));

        System.out.println("Distance: "
                + selected.distanceKm() + " km");

        System.out.println("CO2: "
                + selected.carbonEmission());

        for (Route route : routes) {
            if (route == null) {
                System.out.println("Null route rejected: missing data");
                continue;
            }

            if (!route.available()) {
                System.out.println(route.id()
                        + " rejected: unavailable");
            } else if (RouteCosts.totalCost(route, request)
                    > request.maxBudget()) {
                System.out.println(route.id()
                        + " rejected: budget exceeded");
            } else if (route.estimatedMinutes()
                    + route.trafficLevel() * 5L
                    > request.maxDeliveryMinutes()) {
                System.out.println(route.id()
                        + " rejected: deadline exceeded");
            }
        }
    }
}
