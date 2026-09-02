import math


def add_query_filters(query, filters):
    if not filters:
        return query

    matchers = ", ".join(f'{k}="{v}"' for k, v in filters.items())

    if "{" in query:
        return query.replace("}", f", {matchers}}}", 1)

    return query + "{" + matchers + "}"


def get_mean_and_std(data: list):
    n = len(data)
    mean = sum(data) / n
    if n > 1:
        variance = sum((x - mean) ** 2 for x in data) / (n - 1)
        std_dev = math.sqrt(variance)
    else:
        std_dev = 0.0

    return {
        "mean": mean,
        "std_dev": std_dev,
        "iterations": n,
    }
