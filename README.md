# 💰 Gagyebu, 가계부
> 나의 재정 상태를 보기 쉬운 ui로 전체적인 통계를 내릴 수 있는 앱 입니다.

## 프로젝트 기간
> November 16, 2022 ~ December 20, 2022

## 제작 동기
> 2022년 ICT 인턴십중 진행한 프로젝트 입니다.
> ...
> 
<br/>

## 🧞‍♀️ 프로젝트 참여자

|[![](https://github.com/H-Zoon.png?size=100)](https://github.com/H-Zoon) |[![](https://github.com/eunjjungg.png?size=100)](https://github.com/eunjjungg) |
|:---:|:---:|
| **HyunJoon Choi** | **EunJung Jung** | 

<br/><br/>

## 📌 [@HyunJoon Choi](https://github.com/H-Zoon) : 사용 기술

- Android Studio, Kotlin
- MVVM
- Live Data, Data Binding
- Android Jetpack (Compose UI, Android KTX)
- Custom View
- Room
- 협업 - Jira, Gitflow, Bitbucket, Confluence, Figma

<br/><br/>

## 💻 [@HyunJoon Choi](https://github.com/H-Zoon) : Native 담당 부분

- 프로젝트 세팅
- 메인 화면 레이아웃 구성
- 사용자의 데이터 변화에 대한 반응형 로직 구현
- 사용자가 추가한 데이터 필터링 로직 구현 (날짜별, 소비유형)
- 데이터 구조
<br/><br/>

## MainActivity **Layout structure**

<img width="892" alt="스크린샷 2023-05-01 오후 8 59 12" src="[https://user-images.githubusercontent.com/43941511/235448800-3ec39cd9-2429-4343-a24f-62cb1c2c8f6d.png](https://user-images.githubusercontent.com/43941511/235448800-3ec39cd9-2429-4343-a24f-62cb1c2c8f6d.png)">

## MainActivity DataFlow

<img width="971" alt="스크린샷 2023-05-01 오후 8 45 48" src="[https://user-images.githubusercontent.com/43941511/235447449-28af1a80-d7e1-44b0-a784-0b6452ab93e0.png](https://user-images.githubusercontent.com/43941511/235447449-28af1a80-d7e1-44b0-a784-0b6452ab93e0.png)">

# Application Function 1 - DataControl

[https://user-images.githubusercontent.com/43941511/235449716-33b6668b-29e2-42cc-8265-1ec04f7c38c6.mp4](https://user-images.githubusercontent.com/43941511/235449716-33b6668b-29e2-42cc-8265-1ec04f7c38c6.mp4)

- 데이터가 포함된 날짜, 필터링, 총액, 수입과 지출금액, 소비 세부정보를 볼 수 있는 메인 화면
- 상세 데이터를 왼쪽으로 스와이프 하여 데이터 삭제기능 구현
- 데이터를 삭제한 후 스넥바를 통해 삭제 완료를 노출하고 3초 이내 데이터 복구 기능 구현
- 상세 데이터를 오른쪽으로 스와이프 하여 데이터 수정기능 구현
- 사용자의 이벤트를 받아 실시간으로 메인 화면의 각 요소를 변경할 수 있도록 설계

## User Item save - Room Database

- 사용자의 가계부 항목 (수입/지출여부, 제목, 금액, 카테고리) 은 Android Room Database를 통해 저장.
- 각 항목의 필요한 쿼리는 Repository에서 명세하여 사용.

### Repository Example

```

    //필터링 없음 + 금액, 일자 사용자 입력에 따라 정렬
    @Query("SELECT * FROM ItemEntity WHERE year= :year AND month = :month ORDER BY "+
            "CASE :order WHEN 'day' THEN day END DESC," +
            "CASE :order WHEN 'amount' THEN amount END DESC")
    fun sortDay(year: Int, month: Int, order: String) : Flow<List<ItemEntity>>

    //수입 필터링 + 월/금액정렬
    @Query("SELECT * FROM ItemEntity WHERE category = '수입' AND year= :year AND month = :month ORDER BY "+
            "CASE :order WHEN 'day' THEN day END DESC," +
            "CASE :order WHEN 'amount' THEN amount END DESC")
    fun sortInIncome(year: Int, month: Int, order: String) : Flow<List<ItemEntity>>

    //지출 필터링 + 월/금액정렬
    @Query("SELECT * FROM ItemEntity WHERE NOT category = '수입' AND year= :year AND month = :month ORDER BY "+
            "CASE :order WHEN 'day' THEN day END DESC," +
            "CASE :order WHEN 'amount' THEN amount END DESC")
    fun sortInSpend(year: Int, month: Int, order: String) : Flow<List<ItemEntity>>

```

- 사용자 데이터는 FlowData로 반환받고 DomainLayer에서 수집. LiveData로 변환하도록 구현

```

    //사용자 item
    val itemFlow: LiveData<List<ItemEntity>> = itemGetOption.flatMapLatest {
        itemRepository.itemGet(it)
    }.asLiveData()

```

## Compose (MainUI)

- 도메인 레이아웃의 데이터 변화를 감지하여 Compose의 LazyColumn 값을 초기화 하도록 구현

```

val itemValue by MainViewModel.itemFlow.observeAsState()

...

 itemValue?.let { it ->
                ItemList(it, listState = listState,
                    DismissDelete = {
                        ItemRepo.deleteItem(it.id)
                    },
                    DismissUpdate =
                        val updateData = UpdateDate(
                            id = it.id,
                            date = "${it.year}-${it.month}-${it.day}",
                            title = it.title,
                            amount = it.amount,
                            category = it.category
                        )

```

# Application Function 2 - item filtering

[https://user-images.githubusercontent.com/43941511/235449805-a4460b9a-d0f6-4994-a848-2061be3bb1cb.mp4](https://user-images.githubusercontent.com/43941511/235449805-a4460b9a-d0f6-4994-a848-2061be3bb1cb.mp4)

- 사용자의 가계내역 (전체, 수입, 지출)과 정렬방식(금액, 날짜)을 입력받아 해당 내용으로 필터링 할 수 있도록 구현
- 해당 데이터는 어플리케이션이 종료되고 다시 실행되어도 저장될 수 있도록 Android DataStore를 이용하여 구현

## **Save filtering information -** DataStore

```yaml
private val Context.dataStore by preferencesDataStore(name = "option_state")

class OptionState(private val context: Context) {

    private val year = intPreferencesKey("year") // 년도 저장
    private val month = intPreferencesKey("month") // 달 저장
    private val filter = stringPreferencesKey("filter") // 필터값 저장
    private val order = stringPreferencesKey("order") // 정렬값 저장

    val yearFlow : Flow<Int> = context.dataStore.data
        .map { Preferences ->  Preferences[year] ?: YEAR}

    val monthFlow : Flow<Int> = context.dataStore.data
        .map { Preferences ->  Preferences[month] ?: MONTH}

    val filterFlow : Flow<String> = context.dataStore.data
        .map { Preferences ->  Preferences[filter] ?: SelectableOptionsEnum.DEFAULT.toString()}

    val orderFlow : Flow<String> = context.dataStore.data
        .map { Preferences ->  Preferences[order] ?: SelectableOptionsEnum.day.toString()}

    suspend fun setYear(value : Int){
        context.dataStore.edit {
                Preferences -> Preferences[year] = value
        }
    }

    suspend fun setMonth(value : Int){
        context.dataStore.edit {
                Preferences -> Preferences[month] = value
        }
    }

    suspend fun setFilter(value : String){
        context.dataStore.edit {
                Preferences -> Preferences[filter] = value
        }
    }

    suspend fun setOrder(value : String){
        context.dataStore.edit {
                Preferences -> Preferences[order] = value
        }
    }
}
```

## show filtering statement

필터링 적용 상태일때 아이콘 색상 변화를 주어 직관적으로 확인할 수 있도록 추가

```yaml
//필터링 상태에 따라 icon 색상 변경
        viewModel.filterState.observe(this) {
            when (it) {
                true -> binding.filter.imageTintList =
                    ColorStateList.valueOf(android.graphics.Color.parseColor("#E74141"))

                false -> binding.filter.imageTintList =
                    ColorStateList.valueOf(android.graphics.Color.parseColor("#6D6D6D"))
            }
        }
```

## ProduceActivity **Layout structure**

## ProduceActivity DataFlow

## Application Function 3 - item input

날짜, 제목, 금액, 소비/지출 여부를 입력받아 저장

고려사항

- 데이터가 올바른 데이터인지
- 저장이 성공/실패했는지
- 사용자가 모든 데이터를 입력했는지 (버튼을 활성화 해도 괜찮은지)
