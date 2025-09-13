while True:
    try:
        strlists:list[str] = input("enter the lists of no. (sepreated by space): ")
        lists:list[int] = list(map(int, strlists.split()))
        target:int = int(input("Enter the no. to target: "))
    except (ValueError):
        print("Err pls Enter it again with proper value")
    else:
        break
class solutions:
    def solution(lists:list[int],target):
        for i in range(len(lists)):
            for d in reversed(range(len(lists))):
                sum = lists[i]+lists[d]
                if i == d:
                    continue
                if sum == target:
                    return [i,d]
                else:
                    continue
                    return f"Sum of targeted no. of this lists {lists} is not found"
print(solutions.solution(lists,target))