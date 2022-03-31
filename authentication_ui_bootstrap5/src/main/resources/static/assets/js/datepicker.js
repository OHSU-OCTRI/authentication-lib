$(function () {
  const inputGroupSelector = '.input-group.date-control';
  const inputSelector = 'input[data-provide=datepicker]';
  const appendSelector = '.input-group-append';

  if (typeof $.fn.datepicker === 'function') {
    // attach date pickers to inputs
    $(inputSelector).datepicker({
      // 'yy' is actually 4-digit year
      dateFormat: 'yy-mm-dd',
      changeMonth: true,
      changeYear: true,
      yearRange: 'c-120:c+10'
    });

    // when the input group add-on is clicked, toggle the picker on the related input
    // the event is delegated to the input group div to simplify locating the input
    $(inputGroupSelector).on('click', appendSelector, function (evt) {
      evt.stopPropagation();
      let pickerElement = $(evt.delegateTarget).find(inputSelector);
      let pickerVisible = pickerElement.datepicker('widget').is(':visible');
      pickerElement.datepicker(pickerVisible ? 'hide' : 'show');
    });
  }
});
