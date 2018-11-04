/**
 *
 * @author by Aleksei_Cherniavskii
 */

$(document).ready(function () {
    $('#addFile').click(function () {
        var $fileForm = $('#importFileForm');
        var fileIndex = $fileForm.find('.upload-file').length;
        var appender;
        if (fileIndex == 0) {
            appender = $fileForm.find('.add-file');
        } else {
            appender = $fileForm.find('.upload-file').last();
        }
        appender.after('<div class="form-group upload-file">'
                       + '<label for="file_' + fileIndex + '">File ' + (fileIndex + 1) + '</label>'
                       + '<div class="input-group">'
                       + '<input id="file_' + fileIndex + '" name="file" type="file" value="Browse File" class="form-control-file"/>'
                       + '</div>'
                       + '</div>');
    });
});