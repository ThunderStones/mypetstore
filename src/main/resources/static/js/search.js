$(function () {
    let searchText = $('#searchText');

    $.widget('custom.categoryAutoComplete', $.ui.autocomplete, {
        _create: function () {
            this._super();
            this.widget().menu('option', 'items', '> :not(.ui-autocomplete-category)');
        },
        _renderMenu: function (ul, items) {
            let that = this, currentCategory = '';
            $.each(items, function (index, item) {
                let li;
                if (item.category !== currentCategory) {
                    ul.append(`<li class='ui-autocomplete-category'>${item.category}</li>`);
                    currentCategory = item.category;
                }
                li = that._renderItemData(ul, item);
                if (item.category) {
                    li.attr('aria-label', `${item.category} : ${item.label}`)
                }
            });
        }
    })

    searchText.categoryAutoComplete({
        minLength: 1,
        delay: 500,
        source: (request, response) => {
            $.ajax({
                type: 'GET',
                url: 'searchAutoComplete?' + 'keywords=' + searchText.val(),
                success: (data) => {
                    response($.map(data, item => {
                        console.log(item.name + item.categoryId)
                        return {label: item.name, category: (item.categoryId).slice(0, 1) + item.categoryId.slice(1).toLowerCase(), id: item.productId}
                    }));

                }
            });
        },
        select: (event, ui) => {
            window.location.href = 'viewProduct?productId=' + ui.item.id;
        }

    })

    $('area').mouseenter(function (e) {

        let area = e.currentTarget;
        $.ajax({
            type: 'GET',
            url: 'previewCategory',
            data: {categoryId: area.alt.toUpperCase()},
            success: (data) => {
                if (area.hasChildNodes()) {
                    $(area).children().css('top', e.clientY)
                    $(area).children().css('left', e.clientX)
                    return;
                }
                let $previewDiv = $(`<div class="preview" style="top: ${e.clientY}px; left: ${e.clientX}px;"><ul></ul></div>`)
                $previewDiv.slideUp(100, 'swing')
                let $ul = $previewDiv.children(':nth-child(1)')
                for (let datum of data) {
                    let arr = datum.description.match(/(<.*>)(.*)/)
                    if (arr !== null) {
                        $ul.append(`<li><a href="viewProduct?productId=${datum.productId}">${arr[1].replaceAll('\\', '')}<span><label class="name">${datum.name}</label>: <label class="des">${arr[2]}</label></span></a></li>`)
                    } else {
                        $ul.append(`<li><a href="viewProduct?productId=${datum.productId}"><img src="/images/NoImage.gif"><span><label class="name">${datum.name}</label>: <label class="des">${datum.description}</label></span></a></li>`)
                    }

                }
                $(area).append($previewDiv)
                $previewDiv.slideDown(100, 'swing')

                $(area).mouseleave((e) => {
                    $('div.preview').slideUp(100, 'swing')
                })
                $(area).mouseenter((e) => {
                    $(area).children().slideDown(100, 'swing')
                })
            }
        })
    })



    // $('#SidebarContent a').mouseenter((e) => {
    //     let a = e.currentTarget;
    //     $.ajax({
    //         type: 'GET',
    //         url: 'previewCategory',
    //         data: {categoryId: a.href.split('=')[1]},
    //         success: (data) => {
    //             if ($(a).has('div').length === 1) {
    //                 $(a).children().css('top', e.clientY)
    //                 $(a).children().css('left', e.clientX)
    //                 return;
    //             }
    //             let $previewDiv = $(`<div class="preview" style="top: ${e.clientY}px; left: ${e.clientX}px;"><ul></ul></div>`)
    //             $previewDiv.slideUp()
    //             let $ul = $previewDiv.children(':nth-child(1)')
    //             for (let datum of data) {
    //                 let arr = datum.description.match(/(<.*>)(.*)/)
    //                 $ul.append(`<li><a href="viewProduct?productId=${datum.productId}">${arr[1].replaceAll('\\', '')}<span><label class="name">${datum.name}</label>: <label class="des">${arr[2]}</label></span></a></li>`)
    //             }
    //             $(a).append($previewDiv)
    //             $previewDiv.slideDown()
    //
    //             $(a).mouseleave((e) => {
    //                 $('div.preview').slideUp(300, 'swing')
    //             })
    //             $(a).mouseenter((e) => {
    //                 $(a).children().slideDown(300, 'swing')
    //             })
    //         }
    //     })
    // })
    // $('area').mouseleave((e) => {
    //     let $divPreview = $('div.preview');
    //     if ($divPreview.is(':hover')) {
    //         $divPreview.remove();
    //     }
    // })

});