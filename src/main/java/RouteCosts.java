public class RouteCosts {

    public static double totalCost(
            Route route,
            DeliveryRequest request
    ) {
        double distanceCost = route.distanceKm() * 20;

        double priorityFee =
                request.priority() == DeliveryPriority.URGENT
                        ? 500 : 0;

        return route.cost() + distanceCost + priorityFee;
    }
}
