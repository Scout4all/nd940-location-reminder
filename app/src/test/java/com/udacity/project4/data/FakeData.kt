package com.udacity.project4.data

import androidx.annotation.VisibleForTesting
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
object FakeData {
    //
    val reminderDataItem1 = ReminderDataItem("Work there",
        "one day i will go there","Google Dubai",25.1003948,55.162867,
        "ChIJAAAAAAAAAAARd8D2Azy-09Q")
    val reminderDataItem2= ReminderDataItem("Say Hi to Friend",
        "say hi to other good developer there","Sajilni.com - Event Management Solution Event ticket seller",
        25.095428,55.160083,
        "ChIJAAAAAAAAAAARrShgG2SUNc0")
    val  reminderDataNullTitle= ReminderDataItem(null,
        "daily friend meeting at evening","Caribou Coffee",
        25.104943,55.168110,
        "ChIJAAAAAAAAAAARfyitG2mPJTc")

    val  reminderDataNullLocation= ReminderDataItem("Say Hi to Frien",
        "daily friend meeting at evening",null,
        25.104943,55.168110,
        "ChIJAAAAAAAAAAARfyitG2mPJTc")

    val remindersDTOList = listOf(
        ReminderDTO("Work there",
            "one day i will go there","Google Dubai",25.1003948,55.162867,
            "ChIJAAAAAAAAAAARd8D2Azy-09Q"),

        ReminderDTO("Say Hi to Friend",
            "\n" +
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi eget pretium leo. Fusce tempus sapien viverra leo lobortis lacinia. Pellentesque at pharetra lorem. Nunc finibus tempor hendrerit. Nam pellentesque, eros vestibulum consequat tristique, eros diam sodales neque, quis feugiat ex elit at ligula. Phasellus vehicula viverra scelerisque. Etiam volutpat dui eget lacinia venenatis. Duis vestibulum lobortis lacus, quis ultricies ex lacinia id. Nullam tempor lectus molestie purus cursus, a gravida metus pharetra. Maecenas bibendum justo sed dictum rutrum. Quisque dictum ipsum ut magna mattis, id maximus lacus ultricies.\n" +
                    "\n" +
                    "Curabitur a massa non nunc molestie finibus. Aenean id hendrerit magna. Donec sed nisl a justo porta rhoncus. Curabitur ullamcorper arcu at odio efficitur, in semper libero fringilla. Vestibulum semper placerat leo, ut congue diam iaculis quis. Cras vel dui lorem. Nullam vel sapien massa. Praesent ultricies, nibh ultrices ornare commodo, nulla elit eleifend leo, sit amet mattis magna tortor egestas nulla. Phasellus congue molestie orci, at fermentum massa euismod sit amet. Aenean luctus mauris vitae nulla finibus placerat. Vestibulum eleifend euismod luctus. Nam sagittis elit in eros commodo accumsan. Etiam tristique metus sit amet enim porttitor, in posuere enim porttitor. Nunc porta lorem non ligula eleifend, in vulputate eros placerat.\n" +
                    "\n" +
                    "Aliquam eu nisl eleifend, rhoncus tellus sed, suscipit est. Nulla ut metus sit amet enim commodo ornare ac eget lectus. Nullam blandit dui eu libero sagittis, sed venenatis nunc convallis. Donec erat mauris, ultricies non mi ac, condimentum consectetur mauris. Quisque accumsan ipsum sit amet ligula congue rutrum. Donec sagittis magna a enim scelerisque lacinia. Nulla posuere ultrices finibus. Aenean vel fringilla ante, nec facilisis sem. Mauris consectetur eget metus vitae commodo. In euismod dui ut diam fringilla tempor. In placerat condimentum mi id viverra.\n" +
                    "\n" +
                    "Donec ut augue ut nibh placerat interdum. Nulla dapibus purus vel nunc sodales, eu gravida augue porta. Cras pretium, risus nec euismod volutpat, enim nisi accumsan nunc, ut porttitor mi enim sit amet nunc. Nulla a libero vel justo accumsan accumsan at ut quam. Integer vel risus porta, elementum mauris ac, tempus purus. Praesent posuere, arcu sed dignissim accumsan, dui enim luctus sapien, at gravida massa enim nec nisl. Vestibulum rutrum nisi a mattis lobortis. Aenean dapibus nisl sed mollis cursus. Mauris tempus magna a odio feugiat ullamcorper fermentum eu purus. Donec in tellus finibus, molestie urna et, egestas nulla. Sed eu vestibulum arcu.\n" +
                    "\n" +
                    "In hac habitasse platea dictumst. Mauris ultricies dignissim ultricies. Integer tincidunt pulvinar elit, at hendrerit justo blandit ut. Nam egestas, lacus sodales luctus aliquet, ipsum enim commodo ipsum, eget vestibulum eros risus sed mauris. Suspendisse quis hendrerit dui. Nulla facilisi. Nulla ac imperdiet orci. Donec cursus dolor ut nisi fringilla, in suscipit orci maximus. Donec vitae scelerisque lacus. Nam malesuada nunc vel aliquet pretium. Duis imperdiet quam purus, vitae tempor ligula placerat sed. Nullam condimentum purus felis, nec porta leo pellentesque ac. Nullam malesuada volutpat ipsum, nec mollis odio rutrum vel.","Sajilni.com - Event Management Solution Event ticket seller",
            25.095428,55.160083,
            "ChIJAAAAAAAAAAARrShgG2SUNc0"),

        ReminderDTO("Meet Friend",
            "daily friend meeting at evening","Caribou Coffee",
            25.104943,55.168110,
            "ChIJAAAAAAAAAAARfyitG2mPJTc")
    )




}
