'''
Created on 15.01.2011

@author: kupriyanov
'''
import os, webfonts, json, sys

filename = "f:\\!Develop\\android\\mkFonts\\mkFontsParser\\test\\METADATA"
dirname = "f:\\!Develop\\android\\mkFonts\\mkFontsParser\\test\\googlefontdirectory\\" 

fonts = {
         'version': '1',
         'homepage': 'http://code.google.com/webfonts',
         'donate': '',
         'mirrors': 'http://android.kupriyanov.com/apps/fonts/google/',
         'preview': 'http://android.kupriyanov.com/apps/fonts/google/preview/',
         'fonts':{},
         'designers':None}

fonts_list = []
designers_list = []
designers_dict = {}


'''
    parse font data
'''
for f in os.listdir(dirname):
    if os.path.isdir(os.path.join(dirname, f)):
        try:
            if f == 'designers':
                continue
            font = webfonts.parceFontMetadata(os.path.join(dirname, f))
            fonts_list.insert(len(fonts_list), font['font'])
            '''
            parse designer data
            '''

            #try:
            font['designer'] = webfonts.parceDesignerMetadata(dirname, font['designer'])
            #except IOError:
            #    sys.stderr.write('[e]Failed to parse designer BIO:' + font['designer'].get('name',dirname) + '\n')
            
            designers_list.insert(len(designers_list), font['designer'])
            designers_dict[font['designer']['name']] = font['designer']
        except NameError:
            sys.stderr.write('[w]skip invisible font\n')
        except:
            sys.stderr.write('[w]KeyError\n')

            
            
       

fonts['designers'] = designers_dict
fonts['fonts'] = fonts_list




#fonts = {'fonts': font['font'], 'designers' : font['designer']}


output = json.dumps(fonts, ensure_ascii=False, sort_keys=False, indent=4)

print output

f = open(r'f:\\!Develop\\android\\mkFonts\\mkFonts\\assets\\googlefontdirectory_dev.json', 'w')
f.write(output)
f.close()

################################

if __name__ == '__main__':
    pass






