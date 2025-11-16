def main():
    student_marks:dict[str, list[int]] = {}
    n: int = int(input())
    for _ in range(n):
        name, *line = input().split()
        scores = list(map(float, line))
        student_marks[name] = scores
    query_name: str = input()
    print(f"{sum(student_marks[query_name]) / len(student_marks[query_name]):.2f}")
if __name__ == "__main__":
    main()