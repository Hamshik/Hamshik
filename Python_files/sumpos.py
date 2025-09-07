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
                # elif lists[i] == lists[d]:
                #     continue
                #     return f"{[i,d]} the no. was same so skiping"
                if sum == target:
                    return [i,d]
                else:
                    continue
                    return f"Sum of targeted no. of this lists {lists} is not found"
    def alternativeSolution(lists:list[int],target):
        while (True):
            x = 0
            y = 0
            sum = lists[x] + y
            y += 1
            if y == x:
                continue
            elif y == 0:
                x += 1
            elif sum == target:
                return f"{[x,y]}"
            else:
                continue
                return f'targeted no, was not found in this list u provided {lists}'
print(solutions.alternativeSolution(lists,target))